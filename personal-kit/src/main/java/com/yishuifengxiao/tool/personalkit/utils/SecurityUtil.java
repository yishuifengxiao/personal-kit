package com.yishuifengxiao.tool.personalkit.utils;

import com.yishuifengxiao.common.tool.encoder.DES;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/23 11:52
 * @since 1.0.0
 */
public class SecurityUtil {

    public static String createSalt() {
        String salt = com.yishuifengxiao.common.tool.encoder.Md5.md5Short(System.currentTimeMillis() + "");
        return salt;
    }

    public static String encryptPassword(String salt, String originalPassword) {
        return DES.encrypt(salt, originalPassword);
    }
}
