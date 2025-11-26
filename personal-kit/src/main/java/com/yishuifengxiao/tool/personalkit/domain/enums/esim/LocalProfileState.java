package com.yishuifengxiao.tool.personalkit.domain.enums.esim;

import com.yishuifengxiao.common.tool.entity.RootEnum;

import java.util.Arrays;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
public enum LocalProfileState implements RootEnum {

    Enabled("Enabled", "已启用"),
    Disabled("Disabled", "已禁用"),


    Deleted("Deleted", "已删除");

    LocalProfileState(String code, String enumName) {
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
