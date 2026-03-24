package com.nileonx.leetchat_springboot.service.impl;

import com.nileonx.leetchat_springboot.common.ErrorCode;
import com.nileonx.leetchat_springboot.entities.UserEntity;
import com.nileonx.leetchat_springboot.exception.BusinessException;
import com.nileonx.leetchat_springboot.mapper.IUserMapper;
import com.nileonx.leetchat_springboot.service.IAuthService;
import com.nileonx.leetchat_springboot.service.IOssService;
import com.nileonx.leetchat_springboot.service.IUserService;
import com.nileonx.leetchat_springboot.utils.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.nileonx.leetchat_springboot.utils.JwtUtil.generateToken;
import static com.nileonx.leetchat_springboot.utils.JwtUtil.getExpirationDateFromToken;

@Slf4j
@Component
public class AuthServiceImpl implements IAuthService {
    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private IOssService ossService;
    @Override
    public boolean register(UserEntity userEntity) {
        String upass = userEntity.getUpass();
        userEntity.setUpass( EncryptUtil.encrypt(upass));
        userEntity.setUnick(userEntity.getUname());
        userEntity.setCreatetime(new java.sql.Timestamp(System.currentTimeMillis()));
        userMapper.insert(userEntity);
        return false;
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String,Object>query = new HashMap<>();
        query.put("uname",username);
        UserEntity user =userMapper.selectByMap(query).get(0);
        if(user == null)
        {
            throw new BusinessException(ErrorCode.NO_AUTH,"用户不存在");
        }
        String dbpass = user.getUpass();
        if(!EncryptUtil.decrypt(password,dbpass))
        {
            throw new BusinessException(ErrorCode.NO_AUTH,"用户名或密码错误");
        }
        Map<String,Object> res =new HashMap<>();
        res.put("uid", user.getUid());
        res.put("token",  generateToken(username));
        res.put("account", username);
        res.put("unick", user.getUnick() != null ? user.getUnick() : username);
        String osskey = user.getAvatar();
        res.put("avatar", ossService.getUrl(osskey));
        res.put("failure_time", getExpirationDateFromToken((String) res.get("token")));
        return res;
    }

    @Override
    public void logout() {

    }

    @Override
    public boolean hasPermission(String username, String permission) {
        return false;
    }

    @Override
    public UserEntity getCurrentUser() {
        return null;
    }

    @Override
    public boolean isUserLoggedIn() {
        return false;
    }
    @Override
    public boolean resetPassword(String username, String password) {
        UserEntity user =userMapper.selectByUsername(username);
        if(user == null)
        {
            throw new BusinessException(ErrorCode.NO_AUTH,"用户不存在");
        }
        user.setUpass(EncryptUtil.encrypt(password));
        userMapper.updateById(user);
        return true;
    }
    @Override
    public boolean validatedMail(String username,String mail) {
//        HashMap<String, Object> query = new HashMap<>();
//        query.put("uname",username);
//        UserEntity user =userMapper.selectByMap(query).get(0);
        UserEntity user =userMapper.selectByUsername(username);
        if(user == null)
        {
            throw new BusinessException(ErrorCode.NO_AUTH,"用户不存在");
        }
        if(!user.getEmail().equals(mail))
        {
            throw new BusinessException(ErrorCode.NO_AUTH,"邮箱与用户不匹配");
        }
        return true;
    }
}
