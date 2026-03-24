package com.nileonx.leetchat_springboot.service;

import com.nileonx.leetchat_springboot.entities.UserEntity;
import com.nileonx.leetchat_springboot.vo.UserInfoVO;
import com.nileonx.leetchat_springboot.vo.UserSettingVO;

import java.util.List;
import java.util.Map;

public interface IUserService {
    UserEntity  selectUserById(long UserId);
    UserEntity getUserByUsername(String username);
    UserInfoVO getUserInfoById(Long uid);
    void setOnlineStatus(String uid, int status);

    void updateUserAvatar(String uname, String osskey);

    UserSettingVO getUserSettingById(Long uid);

    void updateUserInfo(String uname, String unick, String selfInfo);
}
