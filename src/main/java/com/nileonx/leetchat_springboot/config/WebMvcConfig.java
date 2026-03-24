package com.nileonx.leetchat_springboot.config;

import com.nileonx.leetchat_springboot.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路由
        registry.addMapping("/**")
                // 设置跨域请求的域名
                .allowedOriginPatterns("*")
                // 设置是否允许带有 cookie 信息
                .allowCredentials(true)
                // 设置允许的请求方式 put get post head  delete
                .allowedMethods("*")
                .maxAge(3600);
    }



    // 设置拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/**");
    }
}

