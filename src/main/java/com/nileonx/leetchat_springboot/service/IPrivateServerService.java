package com.nileonx.leetchat_springboot.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nileonx.leetchat_springboot.entities.PrivateMsgEntity;
import com.nileonx.leetchat_springboot.vo.MsgVO;
import com.nileonx.leetchat_springboot.vo.WindowVO;

import java.util.List;

public interface IPrivateServerService {
    public PageInfo<MsgVO> getMessages(Long wid, int pageNum, int pageSize);
    public  void closeWindow(Long wid);
    public void activeWindow(String fromUname, String toUname);
//    public void createWindow(String fromUname, String toUname);
    public MsgVO savePrivateMsg(Long wid,String fromUname,  PrivateMsgEntity MsgRaw);

    List<WindowVO> getActivateWindows(String uname);
    List<MsgVO> getMessages(Long wid);
    /** 将某窗口标记为已读（更新 last_read_mid 到该会话最新消息） */
    void markWindowRead(String uname, Long wid);
}
