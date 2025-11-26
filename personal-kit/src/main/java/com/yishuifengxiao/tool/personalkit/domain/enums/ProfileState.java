package com.yishuifengxiao.tool.personalkit.domain.enums;

import com.yishuifengxiao.common.tool.entity.RootEnum;

import java.util.Arrays;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ProfileState implements RootEnum {

    Available("available", "可用"),
    Allocated("allocated", "不可用"),

    Linked("linked", "已关联"),


    Confirmed("confirmed", "已确认"),


    Released("released", "已释放"),


    Downloaded("downloaded", "已下载"),


    Installed("installed", "已安装"),

    Error("error", "错误"),

    Unavailable("unavailable", "不可用");

    ProfileState(String code, String enumName) {
        this.code = code;
        this.enumName = enumName;
    }

    private String code;
    private String enumName;

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String enumName() {
        return this.enumName;
    }

    @Override
    public RootEnum of(Object code) {
        return Arrays.stream(values()).filter(value -> value.code().equals(code)).findFirst().orElse(null);
    }
}
