package com.nileonx.leetchat_springboot.service.impl;

import com.nileonx.leetchat_springboot.common.ErrorCode;
import com.nileonx.leetchat_springboot.entities.FriendEntity;
import com.nileonx.leetchat_springboot.entities.FriendRequestEntity;
import com.nileonx.leetchat_springboot.entities.UserEntity;
import com.nileonx.leetchat_springboot.exception.BusinessException;
import com.nileonx.leetchat_springboot.mapper.IFriendMapper;
import com.nileonx.leetchat_springboot.mapper.IFriendRequestMapper;
import com.nileonx.leetchat_springboot.service.IFriendService;
import com.nileonx.leetchat_springboot.service.IOssService;
import com.nileonx.leetchat_springboot.service.IUserService;
import com.nileonx.leetchat_springboot.vo.FriendRequestVO;
import com.nileonx.leetchat_springboot.vo.SimpleUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class FriendServiceImpl implements IFriendService {
    @Autowired
    private IFriendRequestMapper friendRequestMapper;
    @Autowired
    private IUserService userService;
    @Autowired
    private IFriendMapper friendMapper;
    @Override
    public void requestFriend(String fromUsername, String toUsername) {
        log.info("requestFriend: fromUsername={}, toUsername={}", fromUsername, toUsername);
        Long fromId = userService.getUserByUsername(fromUsername).getUid();
        UserEntity user = userService.getUserByUsername(toUsername);
        if (user==null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        else if (user.getUid().equals(fromId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能添加自己为好友");
        }
        Long toId=user.getUid();
        FriendEntity friend = friendMapper.selectByFromUidAndToUid(fromId, toId);
        if (friend != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "对方已经是您的好友");
        }
        log.info("fromId={}, toId={}", fromId, toId);
        FriendRequestEntity friendRequestEntity = friendRequestMapper.selectByFromUidAndToUid(toId, fromId);
        if (friendRequestEntity != null) {//对方已经发过好友请求
           acceptFriend(toUsername, fromUsername);//直接添加好友
           return;
        }

        friendRequestEntity = friendRequestMapper.selectByFromUidAndToUid(fromId, toId);

            if (friendRequestEntity == null){
                // 不存在好友请求
                friendRequestEntity = new FriendRequestEntity();
//                friendRequestEntity = new FriendRequestEntity();
                friendRequestEntity.setToid(toId);
                friendRequestEntity.setFromid(fromId);
                friendRequestEntity.setExpiretime(new java.sql.Timestamp(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7));
                friendRequestEntity.setRequesttime(new java.sql.Timestamp(System.currentTimeMillis()));
                // 1000*60*60*24*7 = 7 days
                try {
                    friendRequestMapper.insert(friendRequestEntity);
                } catch (Exception ce) {
                    log.error("requestFriend error:{}", ce.getMessage());
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "好友请求发送失败");
                }
            } else {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请勿重复发送好友请求");
            }
        }
        @Override
            public void acceptFriend (String fromUsername, String toUsername){
            UserEntity formUser = userService.getUserByUsername(fromUsername);
            UserEntity toUser = userService.getUserByUsername(toUsername);
            FriendRequestEntity friendRequestEntity = friendRequestMapper.selectByFromUidAndToUid(formUser.getUid(), toUser.getUid());
            if (friendRequestEntity == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友请求不存在");
            }else{
                friendRequestEntity.setStatus(1);
                friendRequestMapper.updateById(friendRequestEntity);
                FriendEntity friendEntity = new FriendEntity();
                friendEntity.setFromid(formUser.getUid());
                friendEntity.setToid(toUser.getUid());
                friendMapper.insert(friendEntity);
                FriendEntity friendEntity1 = new FriendEntity();
                friendEntity1.setFromid(toUser.getUid());
                friendEntity1.setToid(formUser.getUid());
                friendMapper.insert(friendEntity1);

            }

//            return false;
        }

        @Override
        public boolean rejectFriend (String username){
            return false;
        }

        @Override
        public boolean removeFriend (String username){
            return false;
        }

        @Override
        public boolean blockFriend (String username){
            return false;
        }

        @Override
        public boolean unblockFriend (String username){
            return false;
        }

        @Override
        public List<T> getFriendList (String username){
            return null;
        }

        @Override
        public List<FriendRequestVO> getFriendRequestList (String username){
            //我收到的好友请求
            Long uid = userService.getUserByUsername(username).getUid();
            List<FriendRequestEntity> receiver = friendRequestMapper.selectByToUid(uid);
            List<FriendRequestVO> receiverVO = new ArrayList<>();
            if(!receiver.isEmpty())
                for(FriendRequestEntity friendRequestEntity : receiver){
                    FriendRequestVO vo = buildFriendRequestVO(friendRequestEntity, FriendRequestVO.RECEIVER);
                    if (vo != null)
                        receiverVO.add(vo);
                }


            //我发出的好友请求
            List<FriendRequestEntity> sender = friendRequestMapper.selectByFromUid(uid);
            List<FriendRequestVO> senderVO = new ArrayList<>();
            if(!sender.isEmpty())
                for(FriendRequestEntity friendRequestEntity : sender){
                    FriendRequestVO vo = buildFriendRequestVO(friendRequestEntity, FriendRequestVO.SENDER);
                    if (vo != null)
                        senderVO.add(vo);
                }
            List<FriendRequestVO> result = new ArrayList<>();
            result.addAll(receiverVO);
            result.addAll(senderVO);
            return result;
        }

    @Override
    public List<SimpleUserVO> getFriends(String username) {
        Long fromuid = userService.getUserByUsername(username).getUid();
        List<Long> fidList = friendMapper.getFriendIdList(fromuid);
        List<SimpleUserVO> userVOList = new ArrayList<>();
        for(Long fid : fidList){
            UserEntity user = userService.selectUserById(fid);
            SimpleUserVO userVO = new SimpleUserVO();
            userVO.setOnline(user.getOnline());
            userVO.setUid(user.getUid());
            userVO.setUname(user.getUname());
            userVO.setUnick(user.getUnick());
            userVO.setAvatar(ossService.getUrl(user.getAvatar()));
            userVOList.add(userVO);
        }
        return userVOList;
    }
    @Autowired
    private IOssService ossService;

    private FriendRequestVO buildFriendRequestVO (FriendRequestEntity friendRequestEntity, Integer type){
            //我发出的
            UserEntity displayUser;
            FriendRequestVO vo = new FriendRequestVO();
            vo.setStatus(0);//默认待处理
            if (type == FriendRequestVO.SENDER) {
                Long displayId = friendRequestEntity.getToid();
                displayUser = userService.selectUserById(displayId);
                vo.setFrom(1);
            } else if (type == FriendRequestVO.RECEIVER) {
                Long displayId = friendRequestEntity.getFromid();
                displayUser = userService.selectUserById(displayId);
                vo.setFrom(0);
            } else {
                throw new RuntimeException("type参数错误");
            }
            vo.setUid(displayUser.getUid());

            vo.setUname(displayUser.getUname());
            vo.setUnick(displayUser.getUnick());
            vo.setAvatar(ossService.getUrl(displayUser.getAvatar()));
            if (friendRequestEntity.getExpiretime().before(new java.sql.Timestamp(System.currentTimeMillis())))
                vo.setStatus(3);//已过期
            else {
                vo.setStatus(friendRequestEntity.getStatus());
                //已拒绝或已同意
            }
            if(vo.getStatus()==0)
                return vo;
            else
                return null;
        }
    }
