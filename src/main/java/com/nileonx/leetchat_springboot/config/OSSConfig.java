package com.nileonx.leetchat_springboot.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OSSConfig {
    @Value("${oss.endpoint}")
    private  String endpoint ;
    @Value("${oss.accessKeyId}")
    private  String accessKeyId ;
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;
    @Bean
    public OSS OSSClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}
