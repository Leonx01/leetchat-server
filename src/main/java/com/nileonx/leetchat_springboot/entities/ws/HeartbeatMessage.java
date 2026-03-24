package com.nileonx.leetchat_springboot.entities.ws;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 心跳消息，无业务字段。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HeartbeatMessage extends BaseMessage {
    public static final String TYPE = "heartbeat";

    public HeartbeatMessage() {
        setType(TYPE);
    }
}
