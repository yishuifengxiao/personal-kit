package com.yishuifengxiao.tool.personalkit.demo;

import com.yishuifengxiao.common.tool.entity.RootEnum;

import java.util.List;

public enum DemoEnum implements RootEnum {
    MAN(1, "男性"),
    WOMAN(2, "女性");

    private Integer code;

    private String name;

    DemoEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
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
    public List<? extends RootEnum> vals() {
        return List.of(values());
    }
}
