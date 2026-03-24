package com.nileonx.leetchat_springboot.controller;

import com.nileonx.leetchat_springboot.common.ErrorCode;
import com.nileonx.leetchat_springboot.service.IAuthService;
import com.nileonx.leetchat_springboot.common.BaseResponse;
import com.nileonx.leetchat_springboot.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
//@Api(tags = {"测试"})
public class LoginController {
//    @Resource
//    private RedisTemplate<String, LoginEntity> redisTemplate;
    @Autowired
    private IAuthService authService;

    @RequestMapping("/user/login")
    public BaseResponse login(@RequestParam String account, @RequestParam String password) {
//        log.info("username: " + uname + " password: " + password);
        Map<String,Object> data;
        try{
             data = authService.login(account, password);
        }catch (Exception e)
        {
            return ResultUtil.error(ErrorCode.NO_AUTH, "用户名或密码错误");
        }
        return ResultUtil.success(data);
    }

}
