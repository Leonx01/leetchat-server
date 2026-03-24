package com.nileonx.leetchat_springboot.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

/**
 * @deprecated 请使用 {@link com.nileonx.leetchat_springboot.entities.ws.BaseMessage} 及其子类
 *             ({@link com.nileonx.leetchat_springboot.entities.ws.HeartbeatMessage},
 *             {@link com.nileonx.leetchat_springboot.entities.ws.ChatMessage},
 *             {@link com.nileonx.leetchat_springboot.entities.ws.SignalMessage})。
 *             本类仅保留以免第三方或历史代码引用。
 */
@Deprecated
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralMsgEntity {
    private Long mid;
    private Long sid;
    //所在窗口的id
    private String type;
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


    private Map<String, Object> iceCandidate;
    private String sourceName;
    private String targetName;
    private Map<String, Object> offer;
    private Map<String, Object> answer;
    private String description;
    private String reason;

}
