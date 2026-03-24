package com.nileonx.leetchat_springboot.controller;

import com.nileonx.leetchat_springboot.common.BaseResponse;
import com.nileonx.leetchat_springboot.common.ErrorCode;
import com.nileonx.leetchat_springboot.common.TokenRequired;
import com.nileonx.leetchat_springboot.exception.BusinessException;
import com.nileonx.leetchat_springboot.service.IOssService;
import com.nileonx.leetchat_springboot.service.IUserService;
import com.nileonx.leetchat_springboot.utils.ResultUtil;
import com.nileonx.leetchat_springboot.vo.UserInfoVO;
import com.nileonx.leetchat_springboot.vo.UserSettingVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    @Autowired
    private IUserService userService;
    @TokenRequired
    @GetMapping(value = "/userinfo")
    public BaseResponse getUserInfo(@RequestParam("uid") Long uid)
    {
        UserInfoVO user = userService.getUserInfoById(uid);

        return ResultUtil.success(user);
    }
    @TokenRequired
    @GetMapping(value = "/userinfo/detail")
    public BaseResponse getUserInfoSetting(@RequestAttribute("uname") String uname)
    {
        Long uid = userService.getUserByUsername(uname).getUid();
        UserSettingVO user = userService.getUserSettingById(uid);

        return ResultUtil.success(user);
    }
    @TokenRequired
    @PostMapping(value = "/userinfo/update")
    public BaseResponse updateUserInfo(@RequestBody UserSettingVO userInfoVO, @RequestAttribute("uname") String uname) {
        userService.updateUserInfo(uname, userInfoVO.getUnick(), userInfoVO.getSelfInfo());
        return ResultUtil.success();
    }
    @Autowired
    private IOssService ossService;
    @TokenRequired
    @PostMapping(value = "/avatar/update")
    public BaseResponse uploadAvatar(@RequestParam("file") MultipartFile file,@RequestParam("fileName") String fileName,@RequestAttribute("uname") String uname) {
        String osskey = null;
        try {
            InputStream inputStream = file.getInputStream();
            Long uid = userService.getUserByUsername(uname).getUid();
            osskey =  ossService.uploadAvatar(inputStream,fileName,uid);
            userService.updateUserAvatar(uname,osskey);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.OSS_ERROR,"头像上传失败");
        }
        Map<String, String> data =new HashMap<>();
        String url = ossService.getUrl(osskey);
        data.put("url",url);
        return ResultUtil.success(data);
    }
}