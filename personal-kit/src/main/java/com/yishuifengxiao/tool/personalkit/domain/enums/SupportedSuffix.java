package com.yishuifengxiao.tool.personalkit.domain.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/29 23:03
 * @since 1.0.0
 */
public enum SupportedSuffix {
    EXCEL_XLS("EXCEL_2003", "xls"),

    //
    EXCEL_XLSX("EXCEL_2007", "xlsx"),

    //
    CSV("CSV", "csv"),

    //
    TXT("TXT", "txt"),

    //
    JSON_LINE("JSON_LINE", "jsonLine"),

    //
    JSON("JSON", "json"),

    ;


    private String name;

    private String suffix;

    SupportedSuffix(String name, String suffix) {
        this.name = name;
        this.suffix = suffix;
    }

    public String getName() {
        return name;
    }

    public String getSuffix() {
        return suffix;
    }

    public static Optional<SupportedSuffix> of(String suffix) {
        return Arrays.stream(values()).filter(v -> v.suffix.equalsIgnoreCase(StringUtils.trim(suffix))).findFirst();
    }
}
