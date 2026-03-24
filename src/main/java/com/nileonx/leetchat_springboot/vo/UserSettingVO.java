package com.nileonx.leetchat_springboot.vo;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
public class UserSettingVO {
    private Long uid;
    private String uname;
    private String avatar;
    private String unick;
    private String email;
    private String selfInfo;
}
