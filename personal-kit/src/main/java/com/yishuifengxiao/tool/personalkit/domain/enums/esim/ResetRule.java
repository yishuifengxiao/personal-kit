package com.yishuifengxiao.tool.personalkit.domain.enums.esim;

import com.yishuifengxiao.common.tool.entity.RootEnum;

import java.util.Arrays;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ResetRule implements RootEnum {

    noReset("noReset", "不可重置"),
    resettable("resettable", "可重置"),
    autoReset("autoReset", "自动重置"),

    autoRecycle("autoRecycle", "自动回收");

    ResetRule(String code, String enumName) {
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
