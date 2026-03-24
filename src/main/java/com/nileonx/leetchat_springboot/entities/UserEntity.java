package com.nileonx.leetchat_springboot.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
@TableName(value = "user")
@Data
public class UserEntity {
    @TableId(type = IdType.AUTO)
    private Long uid;
    private String uname;
    private String unick;
    private String upass;
    private String email;
    private String avatar;
    private String selfinfo;
    private Integer status;
    private Integer online;
    private Timestamp createtime;
    private Date birth;
    private Integer deletebit;
}
