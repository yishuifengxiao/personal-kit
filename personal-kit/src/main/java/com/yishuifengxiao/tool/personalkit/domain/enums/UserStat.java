package com.yishuifengxiao.tool.personalkit.domain.enums;

import java.util.Arrays;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/6-16:46
 * @since 1.0.0
 */
public enum UserStat {
    /**
     * 启用
     */
    ACCOUNT_ENABLE("账号启用", 0),
    /**
     * 启用
     */
    ACCOUNT_DISABLE("账号未启用", 1),

    /**
     * 禁用
     */
    ACCOUNT_EXPIRED("账号过期", 2),
    /**
     * 密码过期
     */
    CREDENTIALS_EXPIRED("密码过期", 3),

    /**
     * 账号锁定
     */
    ACCOUNT_LOCKED("账号锁定", 4);

    private String name;

    private int code;

    private UserStat(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public static UserStat code(int code) {
        return Arrays.stream(values()).filter(v -> v.code == code).findFirst().orElse(null);
    }
}
