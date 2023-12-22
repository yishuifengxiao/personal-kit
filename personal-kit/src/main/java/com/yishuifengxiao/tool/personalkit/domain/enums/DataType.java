package com.yishuifengxiao.tool.personalkit.domain.enums;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/29 23:23
 * @since 1.0.0
 */
public enum DataType {

    TEXT("字符串", false, DataKind.TEXT),

    //
    LONG("整数", false, DataKind.LONG),

    //
    DOUBLE("小数", false, DataKind.DOUBLE),

    //
    DATE("日期", false, DataKind.DATE),

    //
    DATE_TIME("日期时间", false, DataKind.DATE_TIME),

    //
    TIME("时间", false, DataKind.TIME),

    //
    TEXT_ARRAY("字符串数组", true, DataKind.TEXT),

    //
    LONG_ARRAY("整数数组", true, DataKind.LONG),

    //
    DOUBLE_ARRAY("小数数组", true, DataKind.DOUBLE),

    //
    DATE_ARRAY("日期数组", true, DataKind.DATE),

    //
    DATE_TIME_ARRAY("日期时间数组", true, DataKind.DATE_TIME),

    //
    TIME_ARRAY("时间数组", true, DataKind.TIME),

    //
    ANY("其他类型", false, DataKind.TEXT);

    DataType(String name, boolean array, DataKind dataKind) {
        this.name = name;
        this.array = array;
        this.dataKind = dataKind;
    }

    private String name;

    private boolean array;

    private DataKind dataKind;

    public String getName() {
        return name;
    }

    public boolean isArray() {
        return array;
    }

    public DataKind getDataKind() {
        return dataKind;
    }
}
