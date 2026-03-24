package com.nileonx.leetchat_springboot.controller;

import com.nileonx.leetchat_springboot.common.BaseResponse;
import com.nileonx.leetchat_springboot.common.ErrorCode;
import com.nileonx.leetchat_springboot.entities.UserEntity;
import com.nileonx.leetchat_springboot.exception.BusinessException;
import com.nileonx.leetchat_springboot.service.IAuthService;
import com.nileonx.leetchat_springboot.service.IMailService;
import com.nileonx.leetchat_springboot.utils.MailAuthUtil;
import com.nileonx.leetchat_springboot.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class RegisterController {
    private static final int EMAIL_EXPIRED_TIME = 5;
    @Autowired
    private IAuthService authService;
    @Autowired
    private IMailService mailService;
    @Autowired
    private StringRedisTemplate redisTemplate;


    @RequestMapping("/user/authMail")
    public BaseResponse sendMail(@RequestParam String email){
        String verifyCode = redisTemplate.opsForValue().get(email);
        if(StringUtils.isNotBlank(verifyCode))//如果验证码未过期
        {
            throw new BusinessException(ErrorCode.NO_AUTH,"验证码未过期");
        }else{
            String VerifyCode = MailAuthUtil.genVerifyCode();
            mailService.sendMail(email,VerifyCode);
            redisTemplate.opsForValue().set(email,VerifyCode,EMAIL_EXPIRED_TIME, TimeUnit.MINUTES);
            return ResultUtil.success();
            // 放入redis中，并设置过期时间
        }

    }
    @RequestMapping("/password/auth")
    public BaseResponse sendResetAuthMail(@RequestParam String account,@RequestParam String email){
        authService.validatedMail(account,email);
        return sendMail(email);
    }

    @RequestMapping("/user/register")
    public BaseResponse register(@RequestParam String account,@RequestParam String email,@RequestParam String verifyCode,String password){
        if(!verifyCode.equals(redisTemplate.opsForValue().get(email)))
        {
            throw new BusinessException(ErrorCode.VALIDATE_ERROR,"验证码错误");
        }
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setUname(account);
        user.setUpass(password);
        try {
            authService.register(user);
        }catch (Exception e)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败");
        }
        //删除redis中的验证码
        if(Boolean.TRUE.equals(redisTemplate.hasKey(email)))
            redisTemplate.delete(email);

        return ResultUtil.success("注册成功");


    }
    @RequestMapping("/password/reset")
    public BaseResponse resetPassword(@RequestParam  String account,String password){

        log.info("account: " + account + " password: " + password);
        authService.resetPassword(account,password);
        //删除redis中的验证码
//        if(Boolean.TRUE.equals(redisTemplate.hasKey(email)))
//            redisTemplate.delete(email);

        return ResultUtil.success("重置密码成功");
    }
    @RequestMapping("/password/validate")
    public BaseResponse validateMail(@RequestParam String account,@RequestParam String email,@RequestParam String verifyCode){
        if (!verifyCode.equals(redisTemplate.opsForValue().get(email)))
        {
            throw new BusinessException(ErrorCode.VALIDATE_ERROR,"验证码错误");
        }
        if(Boolean.TRUE.equals(redisTemplate.hasKey(email)))
            redisTemplate.delete(email);
        return ResultUtil.success("验证成功");
    }

}
