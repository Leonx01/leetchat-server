package com.nileonx.leetchat_springboot.entities;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
@Data
@TableName(value = "friendrequest")
public class FriendRequestEntity {
    @TableId(type = IdType.AUTO)
    private Long rid;
    private Long fromid;
    private Long toid;
    private Timestamp expiretime;
    private Timestamp requesttime;
    private Integer status;
}
