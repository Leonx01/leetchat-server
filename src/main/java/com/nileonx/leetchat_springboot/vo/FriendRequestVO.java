package com.nileonx.leetchat_springboot.vo;

import com.nileonx.leetchat_springboot.entities.FriendRequestEntity;
import com.nileonx.leetchat_springboot.entities.UserEntity;
import com.nileonx.leetchat_springboot.mapper.IUserMapper;
import com.nileonx.leetchat_springboot.service.impl.UserServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Objects;
@Slf4j
@Data
public class FriendRequestVO {

    public static final Integer SENDER = 0;
    public static final Integer RECEIVER = 1;
    private Long uid;
    private String uname;
    private String unick;
    private String avatar;
    private Integer status;//待处理 0 已过期 1 已拒绝 2 已同意 3
    private Integer from;
//    public FriendRequestVO(FriendRequestEntity friendRequestEntity, Integer type) {
//        this.status = 0;//默认待处理
//        //我发出的
//        UserServiceImpl userService = new UserServiceImpl();
//        UserEntity displayUser;
//        if(Objects.equals(type, SENDER)) {
//            Long displayId = friendRequestEntity.getToid();
//            log.info("displayId:{}",displayId);
//            displayUser = userService.selectUserById(displayId);
//            log.info("displayUser:{}",displayUser);
//
//        }else if(Objects.equals(type, RECEIVER)) {
//            Long displayId = friendRequestEntity.getFromid();
//            log.info("displayId:{}",displayId);
//            displayUser = userService.selectUserById(displayId);
//        }
//        else {
//            throw new RuntimeException("type参数错误");
//        }
//        this.uname = displayUser.getUname();
//        this.unick = displayUser.getUnick();
//        this.avatar = displayUser.getAvatar();
//        if(friendRequestEntity.getExpiretime().before(new Timestamp(System.currentTimeMillis())))
//            this.status = 3;//已过期
//        else{
//            this.status = friendRequestEntity.getStatus();
//            //已拒绝或已同意
//        }

//    }
}
