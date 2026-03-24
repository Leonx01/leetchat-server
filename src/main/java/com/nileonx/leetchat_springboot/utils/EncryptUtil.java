package com.nileonx.leetchat_springboot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.UUID;
@Slf4j
public class EncryptUtil {
    /**
     * 加盐加密
     *
     * @param password 明文密码
     * @param salt     可传递盐值
     * @return 加盐加密的密码
     */
    /**
     * 加盐加密
     *
     * @param password 明文密码
     * @return 加盐加密的密码
     */
    public static String encrypt(String password) {
        // 1.产生盐值
        String salt = UUID.randomUUID().toString().replace("-", "");
        // 2.使用MD5(盐值+明文密码)得到加密的密码
        String finalPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes());
        // 3.将盐值和加密的密码共同返回（合并盐值和加密密码）
        String dbPassword = salt + "$" + finalPassword;
        return dbPassword;
    }
    /**
     * 加盐加密
     *
     * @param password 明文密码
     * @param salt     可传递盐值
     * @return 加盐加密的密码
     */
    public static String encrypt(String password, String salt) {
        // 1.使用(盐值+明文密码)得到加密的密码
        String finalPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes());
        // 2.将盐值和加密的密码共同返回（合并盐值和加密密码）
        String dbPassword = salt + "$" + finalPassword;
        return dbPassword;
    }

    /**
     * 验证加盐加密密码
     *
     * @param password   明文密码（不一定对，需要验证明文密码）
     * @param dbPassword 数据库存储的密码（包含：salt+$+加盐加密密码）
     * @return true=密码正确
     */
    public static boolean decrypt(String password, String dbPassword) {
        boolean result = false;
        if (StringUtils.hasLength(password) && StringUtils.hasLength(dbPassword) &&
                dbPassword.length() == 65 && dbPassword.contains("$")) { // 参数正确
            // 1.得到盐值
            String[] passwrodArr = dbPassword.split("\\$");
            log.info("salt : "+passwrodArr[0]);
            log.info("pass : "+passwrodArr[1]);
//            System.out.println(passwrodArr[0]);
//            System.out.println(passwrodArr[1]);
            // 1.1 盐值
            String salt = passwrodArr[0];
            String checkPassword = encrypt(password, salt);
            log.info("dbPassword\t"+dbPassword);
            log.info("checkPassword\t"+checkPassword);
            if (dbPassword.equals(checkPassword)) {
                result = true;
            }
        }
        return result;
    }

}
