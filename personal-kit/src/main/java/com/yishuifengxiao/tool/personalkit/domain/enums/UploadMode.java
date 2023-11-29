package com.yishuifengxiao.tool.personalkit.domain.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/16-19:31
 * @since 1.0.0
 */
public enum UploadMode {

    /**
     * 启用
     */
    UPLOAD("上传", 0),
    /**
     * 启用
     */
    ANALYSIS("解析", 1);


    private String name;

    private int code;

    private UploadMode(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public static Optional<UploadMode> code(int code) {
        return Arrays.stream(values()).filter(v -> v.code == code).findFirst();
    }
}
