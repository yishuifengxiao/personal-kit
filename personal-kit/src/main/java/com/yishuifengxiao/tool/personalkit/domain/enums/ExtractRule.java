package com.yishuifengxiao.tool.personalkit.domain.enums;

public enum ExtractRule {
    ANY("原始值", DataType.ANY),
    //
    CONVERT_TEXT("转换为单值文本", DataType.TEXT),
    //
    CONVERT_LONG("转换为单值整数", DataType.LONG),
    //
    CONVERT_DOUBLE("转换为单值小数", DataType.DOUBLE),
    //
    SPILT("切割", DataType.TEXT_ARRAY),
    //
    REGEX("正则单值", DataType.TEXT),
    //
    REGEX_ARRAY("正则数组", DataType.TEXT),

    SUBSTRING_INDEX_BEFORE("截取偏移量之前的内容", DataType.TEXT),
    SUBSTRING_INDEX_AFTER("截取偏移量之后的内容", DataType.TEXT),

    SUBSTRING_INDEX_BETWEEN("截取偏移量之间的内容", DataType.TEXT),

    SUBSTRING_CHAR_BEFORE("截取字符之前的内容", DataType.TEXT),
    SUBSTRING_CHAR_AFTER("截取字符之后的内容", DataType.TEXT),

    SUBSTRING_CHAR_BETWEEN("截取字符之间的内容", DataType.TEXT),

    TRIM("去掉空白字符", DataType.TEXT),

    UPPER_CASE("转为大写", DataType.TEXT),

    LOWER_CASE("转为小写", DataType.TEXT),
    ;


    private String name;
    private DataType resultType;

    ExtractRule(String name, DataType resultType) {
        this.name = name;
        this.resultType = resultType;
    }

    public String getName() {
        return name;
    }

    public DataType getResultType() {
        return resultType;
    }
}
