package com.nileonx.leetchat_springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.nileonx.leetchat_springboot.mapper")
@SpringBootApplication
public class LeetchatSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeetchatSpringbootApplication.class, args);
    }

}
