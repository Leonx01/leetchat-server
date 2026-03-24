package com.nileonx.leetchat_springboot.server;

import com.nileonx.leetchat_springboot.config.ServerDecoder;
import com.nileonx.leetchat_springboot.config.ServerEncoder;
import com.nileonx.leetchat_springboot.config.WebSocketConfig;
import com.nileonx.leetchat_springboot.entities.ws.BaseMessage;
import com.nileonx.leetchat_springboot.entities.ws.HeartbeatMessage;
import com.nileonx.leetchat_springboot.entities.ws.SignalMessage;
import com.nileonx.leetchat_springboot.service.IPrivateServerService;
import com.nileonx.leetchat_springboot.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@ServerEndpoint(value = "/online-status",configurator = WebSocketConfig.class, decoders = ServerDecoder.class, encoders = ServerEncoder.class)
public class GlobalUserStatusServer {
    static final int ONLINE = 1;
    static final int OFFLINE = 0;
    private static final long HEARTBEAT_TIMEOUT_MS = 180_000L;
    private static final long REAPER_INTERVAL_MS = 10_000L;
    //全局用户状态服务器
    private static IUserService userService;
    @Autowired
    public void setService(IUserService userService){
        GlobalUserStatusServer.userService = userService;
    }

    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<GlobalUserStatusServer>> globalUserStatusServerMap = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService reaper = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "online-status-reaper");
        t.setDaemon(true);
        return t;
    });
    static {
        reaper.scheduleWithFixedDelay(
                GlobalUserStatusServer::cleanupStaleConnections,
                REAPER_INTERVAL_MS,
                REAPER_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }
    private String uid;
    private Session session;
    private volatile long lastSeenAt = System.currentTimeMillis();

    private static void cleanupStaleConnections() {
        long now = System.currentTimeMillis();
        for (String uid : globalUserStatusServerMap.keySet()) {
            CopyOnWriteArraySet<GlobalUserStatusServer> sessions = globalUserStatusServerMap.get(uid);
            if (sessions == null) {
                continue;
            }
            for (GlobalUserStatusServer server : sessions) {
                boolean closed = server.session == null || !server.session.isOpen();
                boolean timeout = (now - server.lastSeenAt) > HEARTBEAT_TIMEOUT_MS;
                if (closed || timeout) {
                    sessions.remove(server);
                    if (timeout) {
                        log.warn("[WS] heartbeat timeout, uid={}, sessionId={}", uid, server.session != null ? server.session.getId() : "unknown");
                    }
                }
            }
            if (sessions.isEmpty()) {
                globalUserStatusServerMap.remove(uid);
                if (userService != null) {
                    userService.setOnlineStatus(uid, OFFLINE);
                }
            }
        }
    }

    private void cleanupCurrentConnection(String reason) {
        if (uid == null) {
            return;
        }
        CopyOnWriteArraySet<GlobalUserStatusServer> sessions = globalUserStatusServerMap.get(uid);
        if (sessions != null) {
            sessions.remove(this);
            if (sessions.isEmpty()) {
                globalUserStatusServerMap.remove(uid);
                if (userService != null) {
                    userService.setOnlineStatus(uid, OFFLINE);
                }
            }
        }
        log.info("用户{}下线, reason={}", uid, reason);
    }

    @OnClose
    public void onClose() {
        cleanupCurrentConnection("close");
    }
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        String uname = session.getUserProperties().get("uname").toString();
        this.uid = userService.getUserByUsername(uname).getUid().toString();
        this.lastSeenAt = System.currentTimeMillis();
        globalUserStatusServerMap.computeIfAbsent(uid, key -> new CopyOnWriteArraySet<>()).add(this);
        log.info("【全局WebSocket日志】用户{}上线", uid);
        userService.setOnlineStatus(uid,ONLINE);
    }
    @OnMessage
    public void onMessage(@RequestBody BaseMessage message, Session session){
        this.lastSeenAt = System.currentTimeMillis();
        if (message instanceof HeartbeatMessage) {
            log.debug("[WS] heartbeat from uid={}", uid);
            return;
        }
        if (!(message instanceof SignalMessage)) {
            log.warn("[WS] unsupported message type: {}", message != null ? message.getType() : null);
            return;
        }
        SignalMessage signal = (SignalMessage) message;
        String type = signal.getType();
        String sourceUid = this.uid;
        String targetUid = signal.getTargetName();
        signal.setSourceName(sourceUid);
        if (targetUid == null || targetUid.isEmpty() || "null".equalsIgnoreCase(targetUid)) {
            log.warn("[WS] invalid message: targetName empty, type={}, source={}", type, sourceUid);
            return;
        }
        log.info("[WS] message type={}, source={}, target={}", type, sourceUid, targetUid);
        if("offer".equals(type)||"answer".equals(type)||"reject".equals(type)||"ice".equals(type)){
            CopyOnWriteArraySet<GlobalUserStatusServer> targetSessions = globalUserStatusServerMap.get(targetUid);
            if(targetSessions != null && !targetSessions.isEmpty()){
                for (GlobalUserStatusServer targetSession : targetSessions) {
                    if (targetSession.session != null && targetSession.session.isOpen()) {
                        targetSession.session.getAsyncRemote().sendObject(signal);
                    }
                }
                log.info("[WS] forward type={} from {} to {}", type, sourceUid, targetUid);
            }else{
                if ("offer".equals(type))
                {
                    log.info("[WS] target offline, reject offer, target={}", targetUid);
                    signal.setType("reject");
                    signal.setDescription("对方不在线");
                    CopyOnWriteArraySet<GlobalUserStatusServer> sourceSessions = globalUserStatusServerMap.get(sourceUid);
                    if (sourceSessions != null) {
                        for (GlobalUserStatusServer sourceSession : sourceSessions) {
                            if (sourceSession.session != null && sourceSession.session.isOpen()) {
                                sourceSession.session.getAsyncRemote().sendObject(signal);
                            }
                        }
                    }
                }
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("Global websocket error, sessionId={}", session != null ? session.getId() : "unknown", error);
        cleanupCurrentConnection("error");
    }

    /**
     * 向指定用户推送消息（用于跨会话私信通知：接收方未打开该会话时通过此通道推送）
     */
    public static void sendToUser(String uid, Object payload) {
        if (uid == null || uid.isEmpty()) return;
        CopyOnWriteArraySet<GlobalUserStatusServer> sessions = globalUserStatusServerMap.get(uid);
        if (sessions == null || sessions.isEmpty()) return;
        for (GlobalUserStatusServer server : sessions) {
            try {
                if (server.session != null && server.session.isOpen()) {
                    server.session.getAsyncRemote().sendObject(payload);
                } else {
                    sessions.remove(server);
                }
            } catch (Exception e) {
                log.warn("[WS] sendToUser uid={} failed", uid, e);
                sessions.remove(server);
            }
        }
        if (sessions.isEmpty()) {
            globalUserStatusServerMap.remove(uid);
            if (userService != null) {
                userService.setOnlineStatus(uid, OFFLINE);
            }
        }
    }
}
