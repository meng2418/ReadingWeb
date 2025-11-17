package com.weread.util;

import java.security.SecureRandom;

/**
 * SecurityUtils - 安全相关工具类
 * 负责生成随机验证码等。
 */
public class SecurityUtils {

    private static final String DIGITS = "0123456783";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private SecurityUtils() {
        // 防止实例化
    }

    /**
     * 生成指定长度的纯数字随机验证码
     * 使用 SecureRandom 保证随机数的安全性。
     *
     * @param length 验证码长度
     * @return 随机数字验证码字符串
     */
    public static String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(DIGITS.charAt(SECURE_RANDOM.nextInt(DIGITS.length())));
        }
        return sb.toString();
    }
    
    // 可以在这里添加其他安全相关方法，例如：
    // public static Long getCurrentUserId() { ... } // 从 SecurityContext 中获取当前用户ID
}