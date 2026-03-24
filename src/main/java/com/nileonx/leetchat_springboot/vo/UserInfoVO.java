package com.nileonx.leetchat_springboot.vo;

import io.swagger.models.auth.In;
import lombok.Data;

import java.sql.Timestamp;
@Data
public class UserInfoVO {
    private String uname;
    private String avatar;
    private String unick;
    private Timestamp createtime;
    private String selfinfo;
    private Integer online;
}
