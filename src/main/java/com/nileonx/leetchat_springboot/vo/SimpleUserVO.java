package com.nileonx.leetchat_springboot.vo;

import lombok.Data;

@Data
public class SimpleUserVO {
    private String uname;
    private String unick;
    private String avatar;
    private Integer online;
    private Long uid;
}
