package com.nileonx.leetchat_springboot.config;

import javax.websocket.DecodeException;
import javax.websocket.EndpointConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nileonx.leetchat_springboot.entities.ws.BaseMessage;

public class ServerDecoder implements javax.websocket.Decoder.Text<BaseMessage> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public BaseMessage decode(String s) throws DecodeException {
        try {
            return mapper.readValue(s, BaseMessage.class);
        } catch (Exception e) {
            throw new DecodeException(s, "Error decoding message.", e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        // Implement if needed
        return true;
    }

    @Override
    public void init(EndpointConfig config) {
        // Custom initialization logic, if needed
    }

    @Override
    public void destroy() {
        // Cleanup logic, if needed
    }
}
