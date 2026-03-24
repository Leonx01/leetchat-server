package com.nileonx.leetchat_springboot.entities.ws;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * WebRTC 信令消息：offer / answer / ice / reject。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SignalMessage extends BaseMessage {
    private String sourceName;
    private String targetName;
    /** SDP offer，JSON 对象 */
    private Map<String, Object> offer;
    /** SDP answer，JSON 对象 */
    private Map<String, Object> answer;
    /** ICE candidate，JSON 对象 */
    private Map<String, Object> iceCandidate;
    private String description;
    private String reason;
}
