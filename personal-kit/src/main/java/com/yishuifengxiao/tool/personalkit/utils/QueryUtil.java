package com.yishuifengxiao.tool.personalkit.utils;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.extractor.SimpleFieldExtractor;
import com.yishuifengxiao.common.tool.bean.ClassUtil;
import jakarta.persistence.Column;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/21 19:23
 * @since 1.0.0
 */
public class QueryUtil {

    private final static FieldExtractor fieldExtractor = new SimpleFieldExtractor();


    public static String createCountSql(String sql) {

        return "select count(*) from (" + sql + ") AS subquery";
    }


    public static <T> String createAndSql(T val, boolean superClass, String alian) {
        String sql = createSql(val, superClass, alian);
        return StringUtils.isBlank(sql) ? "" : " and " + sql + " ";
    }

    public static <T> String createSql(T val, boolean superClass) {
        return createSql(val, superClass, null);
    }

    public static <T> String createSql(T val, boolean superClass, String alian) {
        Class clazz = superClass ? val.getClass().getSuperclass() : val.getClass();
        String alianName = StringUtils.isBlank(alian) ? fieldExtractor.extractTableName(clazz) + "_01" : alian.trim();
        List<FieldValue> fieldValues = extractFields(clazz);
        StringBuilder sql = new StringBuilder();
        fieldValues.forEach(fieldValue -> {
            Object value = fieldExtractor.extractValue(val, fieldValue.getName());
            if (null != value && StringUtils.isNotBlank(value.toString())) {
                if (!(value instanceof String)) {
                    sql.append(alianName).append(".").append(fieldValue.getSimpleName()).append(" = ").append(value);
                } else {
                    sql.append(alianName).append(".").append(fieldValue.getSimpleName()).append(" = '%").append(value).append("%' ");
                }
            }

        });
        return sql.toString();
    }

    private static <T> List<FieldValue> extractFields(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter(v -> !ClassUtil.isSpecialModifier(v)).map(v -> {
            String name = v.getName();
            Column column = v.getDeclaredAnnotation(Column.class);
            if (null == column) {
                column = v.getAnnotation(Column.class);
            }
            Class<?> type = v.getType();
            return new FieldValue(name, null == column ? null : column.name(), type);
        }).collect(Collectors.toList());
    }
}
