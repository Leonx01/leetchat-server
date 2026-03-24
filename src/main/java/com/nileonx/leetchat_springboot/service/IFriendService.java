package com.nileonx.leetchat_springboot.service;

import com.nileonx.leetchat_springboot.vo.FriendRequestVO;
import com.nileonx.leetchat_springboot.vo.SimpleUserVO;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

public interface IFriendService {
    public void requestFriend(String fromUsername, String toUsername);
    public void acceptFriend(String fromUsername, String toUsername);
    public boolean rejectFriend(String username);
    public boolean removeFriend(String username);
    public boolean blockFriend(String username);
    public boolean unblockFriend(String username);
    public List<T> getFriendList(String username);
    public List<FriendRequestVO> getFriendRequestList(String username);
    public List<SimpleUserVO> getFriends(String username);
}
