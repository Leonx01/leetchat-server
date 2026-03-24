package com.nileonx.leetchat_springboot.entities.ws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * WebSocket 消息基类，按 type 多态反序列化为 HeartbeatMessage / ChatMessage / SignalMessage。
 * Java 8 兼容。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = HeartbeatMessage.class, name = "heartbeat"),
    @JsonSubTypes.Type(value = ChatMessage.class, name = "text"),
    @JsonSubTypes.Type(value = ChatMessage.class, name = "image"),
    @JsonSubTypes.Type(value = ChatMessage.class, name = "file"),
    @JsonSubTypes.Type(value = ChatMessage.class, name = "audio"),
    @JsonSubTypes.Type(value = ChatMessage.class, name = "video"),
    @JsonSubTypes.Type(value = SignalMessage.class, name = "offer"),
    @JsonSubTypes.Type(value = SignalMessage.class, name = "answer"),
    @JsonSubTypes.Type(value = SignalMessage.class, name = "ice"),
    @JsonSubTypes.Type(value = SignalMessage.class, name = "reject")
})
public abstract class BaseMessage {
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
