package com.yishuifengxiao.tool.personalkit.domain.enums;

import java.util.Arrays;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/16-19:31
 * @since 1.0.0
 */
public enum RoleStat {

    /**
     * 启用
     */
    ROLE_ENABLE("启用", 1),
    /**
     * 启用
     */
    ROLE_DISABLE("禁用", 0);


    private String name;

    private int code;

    private RoleStat(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public static RoleStat code(int code) {
        return Arrays.stream(values()).filter(v -> v.code == code).findFirst().orElse(null);
    }
}
