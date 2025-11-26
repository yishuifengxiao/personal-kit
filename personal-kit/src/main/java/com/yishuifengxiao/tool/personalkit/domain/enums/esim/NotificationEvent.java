package com.yishuifengxiao.tool.personalkit.domain.enums.esim;

import com.yishuifengxiao.common.tool.entity.RootEnum;

import java.util.Arrays;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
public enum NotificationEvent implements RootEnum {

    down("down", "下载"),
    enable("enable", "启用"),

    disable("disable", "禁用"),


    delete("delete", "删除"),


    rpmEnable("rpmEnable", "RPM启用"),


    rpmDisable("rpmDisable", "RPM禁用"),


    rpmDelete("rpmDelete", "RPM删除"),


    rpmResult("rpmResult", "RPM结果");

    NotificationEvent(String code, String enumName) {
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
