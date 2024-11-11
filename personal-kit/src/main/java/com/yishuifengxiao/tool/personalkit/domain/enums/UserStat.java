package com.yishuifengxiao.tool.personalkit.domain.enums;

import com.yishuifengxiao.common.tool.entity.RootEnum;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/6-16:46
 * @since 1.0.0
 */
public enum UserStat implements RootEnum {
    /**
     * 系统数据
     */
    SYSTEM_INIT("系统数据", -1),
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

    UserStat(String name, int code) {
        this.name = name;
        this.code = code;
    }


    public static Optional<UserStat> code(int code) {
        return Arrays.stream(values()).filter(v -> v.code == code).findFirst();
    }

    @Override
    public Integer code() {
        return this.code;
    }

    @Override
    public String enumName() {
        return this.name;
    }
}
