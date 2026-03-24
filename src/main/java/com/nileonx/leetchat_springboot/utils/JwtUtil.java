package com.nileonx.leetchat_springboot.utils;

import com.nileonx.leetchat_springboot.common.ErrorCode;
import com.nileonx.leetchat_springboot.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret:defaultSecretKeyForDevelopment}")
    private String secretKey;

    private static String SECRET_KEY;

    @PostConstruct
    public void init() {
        SECRET_KEY = secretKey;
    }

    public static String generateToken(String uuid){
        return generateToken(uuid,86400000);
    }
    public static String generateToken(String uuid, long expirationMillis) {
        // 设置过期时间
        Date expirationDate = new Date(System.currentTimeMillis() + expirationMillis);
        System.out.println(expirationDate);
        // 构造 JWT
        String token = Jwts.builder()
                .setSubject(uuid) // 设置 subject，通常是用户标识
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(expirationDate) // 设置过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 使用 HS256 签名算法和密钥生成签名
                .compact();
        log.info("生成的token:{}",token);
        return token;
    }
    public static Long getExpirationDateFromToken(String token) {
        Claims claims = decodeToken(token);
        return claims.getExpiration().getTime();
    }
    public static Claims decodeToken(String jwt) {
        Claims claims;
        try {
             claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY) // 设置密钥
                    .parseClaimsJws(jwt) // 解析 JWT
                    .getBody(); // 获取 Payload 中的信息
            if(claims.getExpiration().before(new Date())) {
                throw new BusinessException(ErrorCode.NO_AUTH,"令牌已过期");
            }
        }catch (Exception e) {
            throw new BusinessException(ErrorCode.NO_AUTH,"非法令牌");
        }
        // 解析 JWT

        return claims;
    }
    public static boolean isExpired(String token) {
        Claims claims = decodeToken(token);
        return claims.getExpiration().before(new Date());
    }

}
