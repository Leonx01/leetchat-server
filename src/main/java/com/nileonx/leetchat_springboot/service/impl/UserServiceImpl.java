package com.nileonx.leetchat_springboot.service.impl;

import com.nileonx.leetchat_springboot.entities.UserEntity;
import com.nileonx.leetchat_springboot.mapper.IUserMapper;
import com.nileonx.leetchat_springboot.service.IOssService;
import com.nileonx.leetchat_springboot.service.IUserService;
import com.nileonx.leetchat_springboot.vo.UserInfoVO;
import com.nileonx.leetchat_springboot.vo.UserSettingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Slf4j
@Component
public class UserServiceImpl implements IUserService {
    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private IOssService ossService;

    @Override
    public UserEntity selectUserById(long UserId) {

        return userMapper.selectByUid(UserId);
    }

    @Override
    public UserEntity getUserByUsername(String uname) {

        return userMapper.selectByUsername(uname);
    }

    @Override
    public UserInfoVO getUserInfoById(Long uid) {
        UserEntity user = userMapper.selectByUid(uid);
        UserInfoVO vo = new UserInfoVO();
        vo.setCreatetime(user.getCreatetime());
        vo.setOnline(user.getOnline());
        vo.setSelfinfo(user.getSelfinfo());
        String osskey = user.getAvatar();
        vo.setAvatar(ossService.getUrl(osskey));
        vo.setUnick(user.getUnick());
        vo.setUname(user.getUname());
        return vo;
    }

    @Override
    public void setOnlineStatus(String uid, int status) {
        userMapper.setOnlineStatus(uid, status);
    }

    @Override
    public void updateUserAvatar(String uname, String osskey) {
        userMapper.updateAvatar(uname, osskey);
    }

    @Override
    public UserSettingVO getUserSettingById(Long uid) {
        UserEntity user = userMapper.selectByUid(uid);
        UserSettingVO vo = new UserSettingVO();
        vo.setSelfInfo(user.getSelfinfo());
        String osskey = user.getAvatar();
        vo.setAvatar(ossService.getUrl(osskey));
        vo.setUnick(user.getUnick());
        vo.setUname(user.getUname());
        vo.setUid(user.getUid());
        vo.setEmail(user.getEmail());
        return vo;
    }

    @Override
    public void updateUserInfo(String uname, String unick, String selfInfo) {
        userMapper.updateUnickAndSelfInfo(uname, unick != null ? unick : "", selfInfo != null ? selfInfo : "");
    }
}
