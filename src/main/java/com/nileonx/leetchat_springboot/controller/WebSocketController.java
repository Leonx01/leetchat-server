package com.nileonx.leetchat_springboot.controller;

import com.nileonx.leetchat_springboot.common.BaseResponse;
import com.nileonx.leetchat_springboot.common.TokenRequired;
import com.nileonx.leetchat_springboot.service.IPrivateServerService;
import com.nileonx.leetchat_springboot.utils.ResultUtil;
import com.nileonx.leetchat_springboot.vo.WindowVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@Slf4j
public class WebSocketController {
    @Autowired
    private IPrivateServerService privateServerService;
    @TokenRequired
    @RequestMapping(value="/window",method= RequestMethod.GET)
    public BaseResponse activateWindow(@RequestParam("uname") String toUname, @RequestAttribute("uname")String fromUname) throws IOException {
        //创建聊天窗口
        privateServerService.activeWindow(fromUname,toUname);
        log.info("激活聊天窗口：{} 与 {}",fromUname,toUname);
        return ResultUtil.success();
    }
    @TokenRequired
    @RequestMapping(value = "/windows",method=RequestMethod.GET)
    public BaseResponse getWindows(@RequestAttribute("uname")String uname) {
        log.info("获取聊天窗口列表：{}",uname);
        List<WindowVO> data = privateServerService.getActivateWindows(uname);
        return ResultUtil.success(data);
    }
    @TokenRequired
    @RequestMapping(value = "/window/close",method=RequestMethod.GET)
    public BaseResponse closeWindow(@RequestParam("wid")String wid) {
        privateServerService.closeWindow(Long.parseLong(wid));
        return ResultUtil.success();
    }
    @TokenRequired
    @RequestMapping(value = "/messages",method=RequestMethod.GET)
    public BaseResponse getMessages(@RequestParam("wid") String wid,
                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                    @RequestParam(value = "size", defaultValue = "20") Integer size,
                                    @RequestAttribute("uname") String uname) {
        if (page != null && page == 1) {
            privateServerService.markWindowRead(uname, Long.parseLong(wid));
        }
        return ResultUtil.success(privateServerService.getMessages(Long.parseLong(wid), page, size));
    }
    @TokenRequired
    @RequestMapping(value = "/window/read", method = RequestMethod.PUT)
    public BaseResponse markWindowRead(@RequestParam("wid") String wid, @RequestAttribute("uname") String uname) {
        privateServerService.markWindowRead(uname, Long.parseLong(wid));
        return ResultUtil.success();
    }
}

