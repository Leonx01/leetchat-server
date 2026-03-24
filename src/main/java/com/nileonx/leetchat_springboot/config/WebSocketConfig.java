package com.nileonx.leetchat_springboot.config;
import com.nileonx.leetchat_springboot.common.ErrorCode;
import com.nileonx.leetchat_springboot.exception.BusinessException;
import com.nileonx.leetchat_springboot.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * WebSocket配置类。
 */
@Slf4j
@Configuration
public class WebSocketConfig extends ServerEndpointConfig.Configurator {

    /**
     * 创建并配置ServerEndpointExporter bean。
     * 此bean用于在Spring应用程序中启用WebSocket端点。
     * @return WebSocket配置的ServerEndpointExporter bean。
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
//         获取HttpSession
        Map<String, List<String>> headers = request.getHeaders();
        String token;
        try{
           token = request.getHeaders().get("Sec-WebSocket-Protocol").get(0);
        }catch (Exception e) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无令牌");
        }
        if(token==null) {
            //如果token为空或者token过期
            throw new BusinessException(ErrorCode.NO_AUTH, "非法令牌");
            //return false;
        }
        log.info("token:{}",token);
        String uname = JwtUtil.decodeToken(token.toString()).getSubject();
        sec.getUserProperties().put("uname", uname);
//        将属性放入Session中
        response.getHeaders().put("Sec-WebSocket-Protocol", Collections.singletonList(token));
        super.modifyHandshake(sec, request, response);

    }
    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        return super.getEndpointInstance(clazz);

    }
    @Override
    public boolean checkOrigin(String originHeaderValue) {
        return super.checkOrigin(originHeaderValue);
    }

}
