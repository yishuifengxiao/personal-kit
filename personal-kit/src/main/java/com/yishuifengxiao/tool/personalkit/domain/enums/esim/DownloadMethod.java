package com.yishuifengxiao.tool.personalkit.domain.enums.esim;

import com.yishuifengxiao.common.tool.entity.RootEnum;

import java.util.Arrays;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
public enum DownloadMethod implements RootEnum {

    defaultMethod("defaultMethod", "默认"),
    activation("activation", "激活码"),
    rootSmDs("rootSmDs", "ROOT-SM-DS"),

    altSmDs("altSmDs", "ALT-SM-DS");

    DownloadMethod(String code, String enumName) {
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
