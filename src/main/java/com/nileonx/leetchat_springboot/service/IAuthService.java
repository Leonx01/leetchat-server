package com.nileonx.leetchat_springboot.service;

import com.nileonx.leetchat_springboot.entities.UserEntity;

import java.util.Map;

public interface IAuthService {
    public boolean register(UserEntity userEntity);
    public Map<String, Object> login(String username, String password);
    public void logout();
    public boolean hasPermission(String username, String permission);
    public UserEntity getCurrentUser();
    public boolean isUserLoggedIn();
    public boolean resetPassword(String username, String password);
    public boolean validatedMail(String username,String mail);


}
