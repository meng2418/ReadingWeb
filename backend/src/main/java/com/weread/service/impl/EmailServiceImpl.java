package com.weread.service.impl;

import com.weread.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationCode(String receiverEmail, String code, String subject) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail); // 从配置的邮箱发送
            message.setTo(receiverEmail); // 接收者
            message.setSubject(subject); // 主题

            // 邮件正文：包含验证码、提醒注意保密、提示有效期
            String text = String.format(
                "【WeRead】您的验证码是：%s，请在5分钟内输入。请勿将验证码透露给任何人。",
                code
            );
            message.setText(text);

            mailSender.send(message);
            System.out.println("INFO: 邮件验证码发送成功到: " + receiverEmail);

        } catch (Exception e) {
            System.err.println("ERROR: 邮件发送失败到 " + receiverEmail + ": " + e.getMessage());
            // 在实际项目中，这里应该记录日志并可能抛出自定义异常
            throw new RuntimeException("邮件发送服务异常");
        }
    }
}