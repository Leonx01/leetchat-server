package com.nileonx.leetchat_springboot.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.nileonx.leetchat_springboot.config.ServerDecoder;
import com.nileonx.leetchat_springboot.config.ServerEncoder;
import com.nileonx.leetchat_springboot.config.WebSocketConfig;
import com.nileonx.leetchat_springboot.entities.ws.BaseMessage;
import com.nileonx.leetchat_springboot.entities.ws.ChatMessage;
import com.nileonx.leetchat_springboot.entities.ws.HeartbeatMessage;
import com.nileonx.leetchat_springboot.entities.ws.SignalMessage;
import com.nileonx.leetchat_springboot.entities.PrivateMsgEntity;
import com.nileonx.leetchat_springboot.service.IPrivateServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
//私聊服务器
@Component
@Slf4j
@ServerEndpoint(value="/private/{sid}",configurator = WebSocketConfig.class, decoders = ServerDecoder.class, encoders = ServerEncoder.class)
public class PrivateWebSocket {
    private static IPrivateServerService privateMsgService;
    @Autowired
    public void setService(IPrivateServerService privateMsgService){
       PrivateWebSocket.privateMsgService = privateMsgService;
    }

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    /**
     * 用户ID
     */
    private String uname;
    private String sid;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    //  注：底下WebSocket是当前类名
//    private static CopyOnWriteArraySet<PrivateWebSocket> webSockets =new CopyOnWriteArraySet<>();
    // 用来存在线连接用户信息
//    private static ConcurrentHashMap<String,Session> sessionPool = new ConcurrentHashMap<>();
    private static final Map<String, CopyOnWriteArraySet<PrivateWebSocket>> socketPool = new ConcurrentHashMap<>();

    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        this.sid = sid;
        this.uname = session.getUserProperties().get("uname").toString();
        try {
            socketPool.computeIfAbsent(sid, key -> new CopyOnWriteArraySet<>()).add(this);
            String uname = session.getUserProperties().get("uname").toString();
            log.info("【websocket消息】当前连接用户：{}  连接到服务器: {}",uname,sid);
            log.info("【websocket消息】当前私信窗口总数为:{}",socketPool.size());

        } catch (Exception e) {
            log.error("【websocket消息】建立连接失败 sid={}", sid, e);
        }
    }

    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        try {
            if (sid != null) {
                CopyOnWriteArraySet<PrivateWebSocket> webSockets = socketPool.get(sid);
                if (webSockets != null) {
                    webSockets.remove(this);
                    if (webSockets.isEmpty()) {
                        socketPool.remove(sid);
                    }
                }
            }
            log.info("【websocket消息】用户退出:{}",uname);
            log.info("【websocket消息】连接关闭");
            log.info("【websocket消息】连接总数为:" + socketPool.size());
        }catch(Exception e){
            log.error("【websocket消息】关闭连接失败", e);
        }
    }

    @OnMessage
    public void onMessage(@RequestBody BaseMessage message, Session session, @PathParam("sid") String sid){
        CopyOnWriteArraySet<PrivateWebSocket> webSockets = socketPool.get(sid);
        if (webSockets == null || webSockets.isEmpty()) {
            log.warn("【websocket消息】sid={} 未找到在线连接，忽略消息", sid);
            return;
        }
        String fromUname = session.getUserProperties().get("uname").toString();
        int connectionCount = webSockets.size();
        long distinctUserCount = webSockets.stream().map(ws -> ws.uname).distinct().count();
        log.info("【websocket消息】当前窗口 sid={}, 连接数={}, 不同用户数={}", sid, connectionCount, distinctUserCount);

        Object outMessage;
        if (message instanceof HeartbeatMessage) {
            log.info("【websocket消息】heartbeat from user :{}", fromUname);
            return;
        }
        if (message instanceof SignalMessage) {
            log.info("【websocket消息】信令消息");
            outMessage = message;
        } else if (message instanceof ChatMessage) {
            ChatMessage chat = (ChatMessage) message;
            PrivateMsgEntity save_msg = new PrivateMsgEntity();
            save_msg.setName(chat.getName());
            save_msg.setSid(Long.parseLong(sid));
            save_msg.setType(chat.getType());
            save_msg.setContent(chat.getContent());
            save_msg.setFromid(chat.getFromid());
            save_msg.setToid(chat.getToid());
            save_msg.setSendtime(chat.getSendtime());
            save_msg.setStatus(chat.getStatus());
            save_msg.setReplymid(chat.getReplymid());
            outMessage = privateMsgService.savePrivateMsg(Long.parseLong(sid), fromUname, save_msg);
            com.nileonx.leetchat_springboot.vo.MsgVO msgVO = (com.nileonx.leetchat_springboot.vo.MsgVO) outMessage;
            if (msgVO != null && msgVO.getTo() != null) {
                String receiverUid = msgVO.getTo().getUid() != null ? msgVO.getTo().getUid().toString() : null;
                String receiverUname = msgVO.getTo().getUname();
                boolean receiverInPool = receiverUname != null && webSockets.stream().anyMatch(ws -> receiverUname.equals(ws.uname));
                if (receiverUid != null && !receiverInPool) {
                    GlobalUserStatusServer.sendToUser(receiverUid, outMessage);
                    log.info("【websocket消息】接收方未连接本会话，已通过全局通道推送 sid={}, toUid={}", sid, receiverUid);
                }
            }
        } else {
            log.warn("【websocket消息】未知消息类型: {}", message != null ? message.getType() : null);
            return;
        }

        for (PrivateWebSocket webSocket : webSockets) {
            try {
                if (!fromUname.equals(webSocket.uname) && webSocket.session.isOpen()) {
                    log.info("【websocket消息】发送消息给{}....:", webSocket.uname);
                    webSocket.session.getAsyncRemote().sendObject(outMessage);
                }
            } catch (Exception e) {
                log.error("【websocket消息】发送消息失败", e);
            }
        }
    }

    /** 发送错误时的处理
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("【websocket消息】发生错误");
        error.printStackTrace();
    }


    // 此为广播消息
    public void broadCastMessage(String message) {
//        log.info("【websocket消息】广播消息:"+message);
//        for(PrivateWebSocket webSocket : webSockets) {
//            try {
//                if(webSocket.session.isOpen()) {
//                    webSocket.session.getAsyncRemote().sendText(message);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    // 此为单点消息
    public void sendOneMessage(String uname, String message) {
//        Session session = sessionPool.get(uname);
//        if (session != null&&session.isOpen()) {
//            try {
//                log.info("【websocket消息】 单点消息:"+message);
//                session.getAsyncRemote().sendText(message);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    // 此为单点消息(多人)
    public void sendMoreMessage(String[] unames, String message) {
//        for(String uname:unames) {
//            Session session = sessionPool.get(uname);
//            if (session != null&&session.isOpen()) {
//                try {
//                    log.info("【websocket消息】 单点消息:"+message);
//                    session.getAsyncRemote().sendText(message);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }

    }

}
