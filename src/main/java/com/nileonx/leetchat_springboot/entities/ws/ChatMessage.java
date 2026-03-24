package com.nileonx.leetchat_springboot.entities.ws;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 私聊消息：文本、图片、文件、音频、视频等。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatMessage extends BaseMessage {
    private Long mid;
    private Long sid;
    private String name;
    private String content;
    @JsonProperty("fromid")
    private Long fromid;
    @JsonProperty("toid")
    private Long toid;
    private Timestamp sendtime;
    private Integer status;
    @JsonProperty("replymid")
    private Long replymid;
}
