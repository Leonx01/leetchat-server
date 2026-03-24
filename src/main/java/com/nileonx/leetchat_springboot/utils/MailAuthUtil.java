package com.nileonx.leetchat_springboot.utils;
import cn.hutool.extra.template.TemplateEngine;
import org.jetbrains.annotations.TestOnly;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.annotation.Resource;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
public class MailAuthUtil {

    public static String genVerifyCode()
    {
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for(int i=0;i<6;i++)
        {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }
}
