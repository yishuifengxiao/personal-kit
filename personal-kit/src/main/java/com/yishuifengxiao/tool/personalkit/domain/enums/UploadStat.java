package com.yishuifengxiao.tool.personalkit.domain.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/29 22:27
 * @since 1.0.0
 */
public enum UploadStat {
    /**
     * 上传中
     */
    UPLOAD_HANDING("上传中", 0),

    /**
     * 上传失败
     */
    UPLOAD_FAIL("上传失败", 1),

    /**
     * 上传成功
     */
    UPLOAD_SUCCESS("上传成功", 2);


    private String name;

    private int code;

    private UploadStat(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public static Optional<UploadStat> code(int code) {
        return Arrays.stream(values()).filter(v -> v.code == code).findFirst();
    }
}
