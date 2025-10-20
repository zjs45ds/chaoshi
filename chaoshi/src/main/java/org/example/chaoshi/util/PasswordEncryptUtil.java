package org.example.chaoshi.util;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

/**
 * 密码加密工具类
 * 使用BCrypt进行密码加密和验证
 */
@Slf4j
public class PasswordEncryptUtil {
    
    /**
     * BCrypt salt轮数，推荐值为10-12
     */
    private static final int BCRYPT_ROUNDS = 10;
    
    /**
     * 密码强度枚举
     */
    public enum PasswordStrength {
        WEAK("弱"),
        MEDIUM("中等"),
        STRONG("强"),
        VERY_STRONG("很强");
        
        private final String description;
        
        PasswordStrength(String description) {
            this.description = description;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
    
    /**
     * 加密密码
     * 
     * @param password 明文密码
     * @return 加密后的密码hash
     */
    public static String encryptPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("密码不能为空");
        }
        
        try {
            String hash = BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
            log.debug("密码加密成功");
            return hash;
        } catch (Exception e) {
            log.error("密码加密失败", e);
            throw new RuntimeException("密码加密失败", e);
        }
    }
    
    /**
     * 验证密码
     * 
     * @param password 明文密码
     * @param hash 存储的密码hash
     * @return 验证结果
     */
    public static boolean verifyPassword(String password, String hash) {
        if (password == null || hash == null) {
            log.warn("密码或hash为空，验证失败");
            return false;
        }
        
        try {
            boolean result = BCrypt.checkpw(password, hash);
            log.debug("密码验证结果: {}", result ? "成功" : "失败");
            return result;
        } catch (Exception e) {
            log.error("密码验证过程中发生异常", e);
            return false;
        }
    }
    
    /**
     * 检查密码强度
     * 
     * @param password 密码
     * @return 密码强度
     */
    public static PasswordStrength checkPasswordStrength(String password) {
        if (password == null) {
            return PasswordStrength.WEAK;
        }
        
        int score = 0;
        
        // 长度检查
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        
        // 包含小写字母
        if (Pattern.compile("[a-z]").matcher(password).find()) score++;
        
        // 包含大写字母
        if (Pattern.compile("[A-Z]").matcher(password).find()) score++;
        
        // 包含数字
        if (Pattern.compile("\\d").matcher(password).find()) score++;
        
        // 包含特殊字符
        if (Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").matcher(password).find()) score++;
        
        return switch (score) {
            case 0, 1, 2 -> PasswordStrength.WEAK;
            case 3, 4 -> PasswordStrength.MEDIUM;
            case 5 -> PasswordStrength.STRONG;
            default -> PasswordStrength.VERY_STRONG;
        };
    }
    
    /**
     * 验证密码是否符合最低安全要求
     * 
     * @param password 密码
     * @return 是否符合要求
     */
    public static boolean isPasswordValid(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        PasswordStrength strength = checkPasswordStrength(password);
        return strength != PasswordStrength.WEAK;
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private PasswordEncryptUtil() {
        throw new UnsupportedOperationException("工具类不能被实例化");
    }
}