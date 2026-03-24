package com.nileonx.leetchat_springboot.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(value = "privatemessage")
public class PrivateMsgEntity {
    @TableId(type = IdType.AUTO)
    private Long mid;
    private Long sid;
    //所在窗口的id
    private String type;
    //消息类型 0:文本 1:图片 2:文件
    private String name;
    private byte[] file;
    //文件信息，存储文件二进制流 文本时为空
    private String  content;
    //文本信息 文件时为空
    private Long fromid;
    private Long toid;
    private Timestamp sendtime;
    private Integer status;
    private Long replymid;
    //回复的消息 默认为0
}
