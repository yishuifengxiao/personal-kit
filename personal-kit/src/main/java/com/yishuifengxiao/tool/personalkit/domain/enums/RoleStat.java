package com.yishuifengxiao.tool.personalkit.domain.enums;

import com.yishuifengxiao.common.tool.entity.RootEnum;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/16-19:31
 * @since 1.0.0
 */
public enum RoleStat implements RootEnum {
    /**
     * 系统
     */
    ROLE_INIT("系统", -1),
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


    public static Optional<RoleStat> code(int code) {
        return Arrays.stream(values()).filter(v -> v.code == code).findFirst();
    }

    @Override
    public Integer code() {
        return this.code;
    }

    @Override
    public String description() {
        return this.name;
    }

    @Override
    public String enumName() {
        return this.name;
    }
}
