package com.nileonx.leetchat_springboot.service.impl;
import com.nileonx.leetchat_springboot.common.ErrorCode;
import com.nileonx.leetchat_springboot.exception.BusinessException;
import com.nileonx.leetchat_springboot.service.IMailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;
@Slf4j
@Component
public class MailServiceImpl implements IMailService {
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;
    @Override
    public void sendMail(String destMail,String VerifyCode) {
//        String VerifyCode = MailAuthUtil.genVerifyCode();
        Context context  = new Context();
        // 设置模板中的变量（分割验证码）
        context.setVariable("verifyCode", Arrays.asList(VerifyCode.split("")));
        String text = templateEngine.process("index.html",context);
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setSubject("【LeetChat】验证码");
            helper.setFrom(username);
            helper.setTo(destMail);
            helper.setSentDate(new Date());
            helper.setText(text,true);
            mailSender.send(message);
        }catch (MessagingException e)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"邮件发送失败");
        }
//        mailSenderailSender.send(mimeMessage);

    }
}
