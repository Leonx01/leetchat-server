package com.nileonx.leetchat_springboot.vo;

import io.swagger.models.auth.In;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class MsgMetaVO {
    private String type;
    private String content;//文本内容或url
    private String name;//文件名
//    private String size;//文件大小
    private Timestamp time;
}
