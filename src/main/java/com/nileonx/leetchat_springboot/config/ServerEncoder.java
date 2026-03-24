package com.nileonx.leetchat_springboot.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerEncoder implements javax.websocket.Encoder.Text<Object> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String encode(Object object) throws javax.websocket.EncodeException {
        try {
            // 使用 ObjectMapper 将对象转换为 JSON 字符串
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new javax.websocket.EncodeException(object, "Error encoding object to JSON: " + e.getMessage());
        }
    }

    @Override
    public void init(javax.websocket.EndpointConfig config) {
        // Custom initialization logic, if needed
    }

    @Override
    public void destroy() {
        // Cleanup logic, if needed
    }
}
