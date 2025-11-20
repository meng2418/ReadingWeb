package com.weread.service;

public interface EmailService {
    /**
     * 发送纯文本验证码邮件
     * @param receiverEmail 接收者邮箱
     * @param vertificationCode 验证码
     * @param subject 邮件主题
     */
    void sendVerificationCode(String receiverEmail, String vertificationCode, String subject);
    
    // 可以在这里添加其他更复杂的 HTML 邮件发送方法
}