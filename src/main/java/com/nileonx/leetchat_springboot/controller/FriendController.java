package com.nileonx.leetchat_springboot.controller;

import com.nileonx.leetchat_springboot.common.BaseResponse;
import com.nileonx.leetchat_springboot.common.TokenRequired;
import com.nileonx.leetchat_springboot.service.impl.FriendServiceImpl;
import com.nileonx.leetchat_springboot.utils.ResultUtil;
import com.nileonx.leetchat_springboot.vo.FriendRequestVO;
import com.nileonx.leetchat_springboot.vo.SimpleUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class FriendController {
    @Autowired
    private FriendServiceImpl friendService;
    @TokenRequired
    @GetMapping("/friendRequest")
    public BaseResponse sendFriendRequest(@RequestParam("uname") String toUname, @RequestAttribute("uname") String fromUname) {
        friendService.requestFriend(fromUname, toUname);
//        return "friend";
        return ResultUtil.success();
    }
    @TokenRequired
    @GetMapping("/friendRequests")
    public BaseResponse getFriendRequests(@RequestAttribute("uname") String uname) {
//        friendService.requestFriend(fromUname, toUname);
//        return "friend";
        List<FriendRequestVO> data = friendService.getFriendRequestList(uname);
        return ResultUtil.success(data);
    }
    @TokenRequired
    @GetMapping("/friendRequest/accept")
    public BaseResponse acceptFriendRequest(@RequestParam("uname") String fromUname, @RequestAttribute("uname") String toUname) {
        friendService.acceptFriend(fromUname, toUname);
        return ResultUtil.success();
    }
    @TokenRequired
    @GetMapping("/friends")
    public BaseResponse getFriends(@RequestAttribute("uname") String uname) {
//        List<FriendRequestVO> data = friendService.getFriendList(uname);
        List<SimpleUserVO>data = friendService.getFriends(uname);
        return ResultUtil.success(data);
    }

}
