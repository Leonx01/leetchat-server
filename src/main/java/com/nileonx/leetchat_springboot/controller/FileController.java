package com.nileonx.leetchat_springboot.controller;

import com.nileonx.leetchat_springboot.common.BaseResponse;
import com.nileonx.leetchat_springboot.common.ErrorCode;
import com.nileonx.leetchat_springboot.common.TokenRequired;
import com.nileonx.leetchat_springboot.exception.BusinessException;
import com.nileonx.leetchat_springboot.service.IOssService;
import com.nileonx.leetchat_springboot.service.IUserService;
import com.nileonx.leetchat_springboot.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class FileController {
    @Autowired
    private IUserService userService;

    @Autowired
    private IOssService ossService;

    @TokenRequired
    @PostMapping("/file")
    public BaseResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName, @RequestAttribute("uname") String uname, @RequestParam("sid") String sid) {
        String osskey = null;
        try {
            InputStream inputStream = file.getInputStream();
            Long uid = userService.getUserByUsername(uname).getUid();
            osskey = ossService.uploadFile(inputStream, fileName, uid, sid);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.OSS_ERROR, "文件上传失败");
        }
        Map<String, String> data = new HashMap<>();
        data.put("key", osskey);
        return ResultUtil.success(data);
    }

    @GetMapping("/file")
    public BaseResponse downloadFile(@RequestParam("downloadKey") String key, @RequestParam("localPath") String localPath) {
        ossService.downloadFile(key, localPath);
        return ResultUtil.success();
    }

    //获取token中的account
    @GetMapping("/fileUrl")
    public BaseResponse getFileUrl(@RequestParam("downloadKey") String key) {
        String url = ossService.getUrl(key);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return ResultUtil.success(data);
    }
}
