package com.yishuifengxiao.tool.personalkit.utils;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.extractor.SimpleFieldExtractor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/21 19:23
 * @since 1.0.0
 */
public class QueryUtil {

    private final static FieldExtractor fieldExtractor = new SimpleFieldExtractor();


    public static <T> String createAndSql(T val, boolean superClass, String alian) {
        String sql = createSql(val, superClass, alian);
        return StringUtils.isBlank(sql) ? "" : " and " + sql + " ";
    }


    public static <T> String createSql(T val, boolean superClass, String alian) {
        Class clazz = superClass ? val.getClass().getSuperclass() : val.getClass();
        String alianName = StringUtils.isBlank(alian) ? fieldExtractor.extractTableName(clazz) + "_01" : alian.trim();
        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(clazz);
        StringBuilder sql = new StringBuilder();
        fieldValues.forEach(fieldValue -> {
            if (null != fieldValue.getValue() && StringUtils.isNotBlank(fieldValue.getValue().toString())) {
                if (!(fieldValue.getValue() instanceof String)) {
                    sql.append(alianName).append(".").append(fieldValue.getSimpleName()).append(" = ").append(fieldValue.getValue());
                } else {
                    sql.append(alianName).append(".").append(fieldValue.getSimpleName()).append(" = '%").append(fieldValue.getValue()).append("%' ");
                }
            }

        });
        return sql.toString();
    }


}
