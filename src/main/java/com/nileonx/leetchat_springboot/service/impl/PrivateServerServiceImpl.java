package com.nileonx.leetchat_springboot.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nileonx.leetchat_springboot.entities.PrivateMsgEntity;
import com.nileonx.leetchat_springboot.entities.PrivateServerEntity;
import com.nileonx.leetchat_springboot.entities.UserEntity;
import com.nileonx.leetchat_springboot.entities.WindowEntity;
import com.nileonx.leetchat_springboot.mapper.IPrivateMsgMapper;
import com.nileonx.leetchat_springboot.mapper.IPrivateServerMapper;
import com.nileonx.leetchat_springboot.mapper.IUserMapper;
import com.nileonx.leetchat_springboot.mapper.IUserWindowReadMapper;
import com.nileonx.leetchat_springboot.mapper.IWindowMapper;
import com.nileonx.leetchat_springboot.service.IOssService;
import com.nileonx.leetchat_springboot.service.IPrivateServerService;
import com.nileonx.leetchat_springboot.vo.MsgMetaVO;
import com.nileonx.leetchat_springboot.vo.MsgVO;
import com.nileonx.leetchat_springboot.vo.SimpleUserVO;
import com.nileonx.leetchat_springboot.vo.WindowVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PrivateServerServiceImpl implements IPrivateServerService {
    private static final String MSG_PAGE_CACHE_PREFIX = "chat:msg:page";
    private static final long MSG_PAGE_CACHE_TTL_SECONDS = 30;

    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private IWindowMapper windowMapper;
    @Autowired
    private IUserWindowReadMapper userWindowReadMapper;
    @Autowired
    private IPrivateMsgMapper privateMsgMapper;
    @Autowired
    private IPrivateServerMapper privateServerMapper;
    @Autowired
    private IOssService ossService;
    @Autowired
    private SensitiveWordBs sensitiveWordBs;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void closeWindow(Long wid) {
        WindowEntity window = windowMapper.selectById(wid);
        window.setActive(0);
        windowMapper.updateById(window);
    }

    @Override
    public void activeWindow(String fromUname, String toUname) {
        Long fromUid = userMapper.gerUserId(fromUname);
        Long toUid = userMapper.gerUserId(toUname);
        WindowEntity window = windowMapper.selectWindowByFromidAndToid(fromUid, toUid);
        if (window == null) {
            createWindow(fromUid, toUid);
        } else {
            window.setLastactivetime(new Timestamp(System.currentTimeMillis()));
            window.setActive(1);
            windowMapper.updateById(window);
        }
    }

    private void createWindow(Long fromUid, Long toUid) {
        WindowEntity window = new WindowEntity();
        window.setFromid(fromUid);
        window.setToid(toUid);
        window.setLastactivetime(new Timestamp(System.currentTimeMillis()));
        window.setActive(1);

        WindowEntity window2 = windowMapper.selectWindowByFromidAndToid(toUid, fromUid);
        if (window2 != null) {
            window.setSid(window2.getSid());
        } else {
            PrivateServerEntity server = new PrivateServerEntity();
            server.setCreatetime(new Timestamp(System.currentTimeMillis()));
            privateServerMapper.insert(server);
            window.setSid(server.getSid());
        }
        windowMapper.insert(window);
        log.info("create private window success fromUid:{} toUid:{}", fromUid, toUid);
    }

    @Override
    public MsgVO savePrivateMsg(Long sid, String fromUname, PrivateMsgEntity msg) {
        msg.setSendtime(new Timestamp(System.currentTimeMillis()));
        log.info("sid={}", sid);
        msg.setSid(sid);

        if ("text".equals(msg.getType())) {
            String content = sensitiveWordBs.replace(msg.getContent());
            msg.setContent(content);
        }

        Long fromUid = userMapper.gerUserId(fromUname);
        msg.setFromid(fromUid);
        if (msg.getToid() == null) {
            msg.setToid(windowMapper.getToidBySidAndFromid(sid, fromUid));
        }
        privateMsgMapper.insert(msg);
        evictMessagePageCacheBySid(sid);
        updateLastActiveTimeForSid(sid);
        return buildMsgVO(msg);
    }

    @Override
    public List<WindowVO> getActivateWindows(String uname) {
        Long uid = userMapper.gerUserId(uname);
        List<WindowEntity> windows = windowMapper.getActivateWindows(uid);
        if (windows == null) {
            return null;
        }

        List<WindowVO> data = new ArrayList<>();
        for (WindowEntity window : windows) {
            WindowVO windowVO = new WindowVO();
            Long touid = window.getToid();
            UserEntity user = userMapper.selectById(touid);
            SimpleUserVO userVO = new SimpleUserVO();
            userVO.setOnline(user.getOnline());
            userVO.setUid(user.getUid());
            userVO.setUname(user.getUname());
            userVO.setUnick(user.getUnick());
            userVO.setAvatar(ossService.getUrl(user.getAvatar()));
            windowVO.setUser(userVO);
            windowVO.setWid(window.getWid());
            windowVO.setLastactivetime(window.getLastactivetime());
            windowVO.setSid(window.getSid());
            int unread = userWindowReadMapper.countUnread(uid, window.getWid(), window.getSid());
            windowVO.setUnreadCount(unread);
            data.add(windowVO);
        }
        return data;
    }

    @Override
    public PageInfo<MsgVO> getMessages(Long wid, int page, int size) {
        Long sid = windowMapper.getServerIdByWid(wid);
        if (sid == null) {
            return new PageInfo<>(new ArrayList<>());
        }
        String cacheKey = buildMessagePageCacheKey(sid, page, size);
        if (page == 1) {
            try {
                Object cached = redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    log.info("[MSG_CACHE] Redis HIT  wid={}, sid={}, page=1, size={}", wid, sid, size);
                    PageInfo<MsgVO> cachedPage = objectMapper.convertValue(cached, new TypeReference<PageInfo<MsgVO>>() {});
                    List<MsgVO> list = objectMapper.convertValue(cachedPage.getList(), new TypeReference<List<MsgVO>>() {});
                    cachedPage.setList(list);
                    return cachedPage;
                }
            } catch (Exception e) {
                log.warn("[MSG_CACHE] Redis read error, wid={}, page=1, size={}", wid, size, e);
            }
            log.info("[MSG_CACHE] Redis MISS wid={}, sid={}, page=1, size={}, query DB", wid, sid, size);
        }
        PageHelper.startPage(page, size);
        List<PrivateMsgEntity> msgs = privateMsgMapper.getMsgBySid(sid);
        PageInfo<PrivateMsgEntity> pageInfo = new PageInfo<>(msgs);
        List<MsgVO> data = buildMsgVOList(msgs);

        PageInfo<MsgVO> result = new PageInfo<>(data);
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setSize(pageInfo.getSize());
        result.setStartRow(pageInfo.getStartRow());
        result.setEndRow(pageInfo.getEndRow());
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setPrePage(pageInfo.getPrePage());
        result.setNextPage(pageInfo.getNextPage());
        result.setIsFirstPage(pageInfo.isIsFirstPage());
        result.setIsLastPage(pageInfo.isIsLastPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setNavigatePages(pageInfo.getNavigatePages());
        result.setNavigateFirstPage(pageInfo.getNavigateFirstPage());
        result.setNavigateLastPage(pageInfo.getNavigateLastPage());
        result.setNavigatepageNums(pageInfo.getNavigatepageNums());
        if (page == 1) {
            try {
                redisTemplate.opsForValue().set(cacheKey, result, MSG_PAGE_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
                log.info("[MSG_CACHE] Redis SET   wid={}, sid={}, page=1, size={}, ttl={}s", wid, sid, size, MSG_PAGE_CACHE_TTL_SECONDS);
            } catch (Exception e) {
                log.warn("[MSG_CACHE] Redis write error, wid={}, page=1, size={}", wid, size, e);
            }
        }
        return result;
    }

    @Override
    public List<MsgVO> getMessages(Long wid) {
        Long sid = windowMapper.getServerIdByWid(wid);
        List<PrivateMsgEntity> msgs = privateMsgMapper.getMsgBySid(sid);
        return buildMsgVOList(msgs);
    }

    private List<MsgVO> buildMsgVOList(List<PrivateMsgEntity> msgs) {
        List<MsgVO> data = new ArrayList<>();
        for (int index = msgs.size() - 1; index >= 0; index--) {
            data.add(buildMsgVO(msgs.get(index)));
        }
        return data;
    }

    private MsgVO buildMsgVO(PrivateMsgEntity msg) {
        MsgVO msgVO = new MsgVO();
        msgVO.setMid(msg.getMid());

        SimpleUserVO fromUserVO = new SimpleUserVO();
        Long fromid = msg.getFromid();
        UserEntity user = userMapper.selectById(fromid);
        fromUserVO.setUid(user.getUid());
        fromUserVO.setUname(user.getUname());
        fromUserVO.setUnick(user.getUnick());
        fromUserVO.setAvatar(ossService.getUrl(user.getAvatar()));
        msgVO.setFrom(fromUserVO);

        Long toid = msg.getToid();
        if (toid != null) {
            UserEntity toUser = userMapper.selectById(toid);
            SimpleUserVO toUserVO = new SimpleUserVO();
            toUserVO.setUid(toUser.getUid());
            toUserVO.setUname(toUser.getUname());
            toUserVO.setUnick(toUser.getUnick());
            toUserVO.setAvatar(ossService.getUrl(toUser.getAvatar()));
            msgVO.setTo(toUserVO);
        }

        MsgMetaVO fromMeta = new MsgMetaVO();
        fromMeta.setTime(msg.getSendtime());
        fromMeta.setType(msg.getType());
        if (!"text".equals(fromMeta.getType())) {
            fromMeta.setContent(ossService.getUrl(msg.getContent()));
        } else {
            fromMeta.setContent(msg.getContent());
        }
        fromMeta.setName(msg.getName());
        msgVO.setFromMessage(fromMeta);

        if (msg.getReplymid() != null) {
            PrivateMsgEntity replyMsg = privateMsgMapper.selectById(msg.getReplymid());
            MsgMetaVO replyMeta = new MsgMetaVO();
            replyMeta.setType(replyMsg.getType());
            if ("file".equals(replyMeta.getType()) || "image".equals(replyMeta.getType())) {
                replyMeta.setContent(ossService.getUrl(replyMsg.getContent()));
            } else {
                replyMeta.setContent(replyMsg.getContent());
            }
            replyMeta.setTime(replyMsg.getSendtime());
            replyMeta.setName(replyMsg.getName());
            msgVO.setToMessage(replyMeta);
        }
        return msgVO;
    }

    private String buildMessagePageCacheKey(Long sid, int page, int size) {
        return String.format("%s:sid:%s:page:%d:size:%d", MSG_PAGE_CACHE_PREFIX, sid, page, size);
    }

    private void evictMessagePageCacheBySid(Long sid) {
        try {
            Set<String> keys = redisTemplate.keys(String.format("%s:sid:%d:page:*:size:*", MSG_PAGE_CACHE_PREFIX, sid));
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("[MSG_CACHE] Redis EVICT sid={}, keys={}", sid, keys.size());
            }
        } catch (Exception e) {
            log.warn("[MSG_CACHE] Redis evict error, sid={}", sid, e);
        }
    }

    @Override
    public void markWindowRead(String uname, Long wid) {
        Long uid = userMapper.gerUserId(uname);
        Long sid = windowMapper.getServerIdByWid(wid);
        if (sid == null) {
            return;
        }
        Long lastReadMid = privateMsgMapper.getMaxMidBySid(sid);
        if (lastReadMid == null) {
            lastReadMid = 0L;
        }
        userWindowReadMapper.upsert(uid, wid, lastReadMid);
    }

    /** 新消息写入后，更新该 sid 下所有窗口的 lastactivetime，使列表按最新消息排序 */
    private void updateLastActiveTimeForSid(Long sid) {
        List<WindowEntity> windows = windowMapper.selectWindowsBySid(sid);
        if (windows == null || windows.isEmpty()) {
            return;
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        for (WindowEntity w : windows) {
            w.setLastactivetime(now);
            windowMapper.updateById(w);
        }
    }
}
