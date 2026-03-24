package com.nileonx.leetchat_springboot.controller;

import com.github.pagehelper.PageInfo;
import com.nileonx.leetchat_springboot.common.BaseResponse;
import com.nileonx.leetchat_springboot.common.TokenRequired;
import com.nileonx.leetchat_springboot.config.OSSConfig;
import com.nileonx.leetchat_springboot.entities.UserEntity;
import com.nileonx.leetchat_springboot.mapper.IPrivateMsgMapper;
import com.nileonx.leetchat_springboot.mapper.IUserMapper;
import com.nileonx.leetchat_springboot.service.IOssService;
import com.nileonx.leetchat_springboot.service.IPrivateServerService;
import com.nileonx.leetchat_springboot.utils.JwtUtil;
import com.nileonx.leetchat_springboot.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class TestController {
    @Autowired
    private OSSConfig ossconfig;
    @Autowired
    private IOssService ossService;
    @Autowired
    private IUserMapper userMapper;
    @TokenRequired
    @RequestMapping("/test")
    public String test(@RequestAttribute String uname){
//        String uname = (String) request.getAttribute("uname");
        log.info("username:{}", uname);
    ossService.uploadFile("C:/Users/Leonx/Downloads/a87d6848-a97b-4c2a-a64e-7456962891ad_自然语言处理.pdf");
        return "success";
    }

    @RequestMapping("/token")
        public String token(@RequestParam String token) {
//        log.info("token:{}", );
//    ossService.uploadFile("C:/Users/Leonx/Downloads/a87d6848-a97b-4c2a-a64e-7456962891ad_自然语言处理.pdf");
            return JwtUtil.generateToken(token);
}
    @RequestMapping("/test2")
    public BaseResponse<UserEntity> test2(@RequestParam Long uid){
        return ResultUtil.success(userMapper.selectByUid(uid));
    }
    @Autowired
    private IPrivateServerService privateServerService;
    @RequestMapping("/testpage")
    public BaseResponse testpage(@RequestParam Long wid, @RequestParam int pageNum, @RequestParam int pageSize){
//        log.info("{}",privateServerService.getMessages(wid,pageNum,pageSize));
        PageInfo info = privateServerService.getMessages(wid,pageNum,pageSize);
        return ResultUtil.success(info.getList());
    }
}
