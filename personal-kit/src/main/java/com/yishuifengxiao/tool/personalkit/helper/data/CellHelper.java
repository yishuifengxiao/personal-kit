package com.yishuifengxiao.tool.personalkit.helper.data;

import com.yishuifengxiao.common.tool.datetime.DateTimeUtil;
import com.yishuifengxiao.common.tool.lang.NumberUtil;
import com.yishuifengxiao.common.tool.text.RegexUtil;
import com.yishuifengxiao.common.tool.utils.CertNoUtil;
import com.yishuifengxiao.common.tool.utils.OsUtils;
import com.yishuifengxiao.tool.personalkit.domain.enums.DataType;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyRow;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 10:55
 * @since 1.0.0
 */
public class CellHelper {

    private final static String REGEX_PHONE = "^\\d{11}$";


    public VirtuallyRow.ExcelCell excelCell(Integer columnIndex, String columnName, String text) {
        if (StringUtils.isBlank(text)) {
            return new VirtuallyRow.ExcelCell(columnIndex, columnName, text, null, null, false, DataType.ANY);
        }
        LocalDateTime time = localDateTime(text.trim());
        if (null != time) {
            return new VirtuallyRow.ExcelCell(columnIndex, columnName, text.trim(), null, time, true, DataType.DATE_TIME);
        }
        if (CertNoUtil.isValid(text.trim())) {
            //身份证
            return new VirtuallyRow.ExcelCell(columnIndex, columnName, text.trim(), null, null, true, DataType.TEXT);
        }
        if (RegexUtil.match(REGEX_PHONE, text.trim())) {
            //手机
            return new VirtuallyRow.ExcelCell(columnIndex, columnName, text.trim(), null, null, true, DataType.TEXT);
        }
        BigDecimal number = NumberUtil.parse(text.trim(),null);
        if (null != number) {
            return new VirtuallyRow.ExcelCell(columnIndex, columnName, text.trim(),
                    number, null, true, text.trim().contains(OsUtils.SPOT) ?
                    DataType.DOUBLE : DataType.LONG);
        }
        return new VirtuallyRow.ExcelCell(columnIndex, columnName, text.trim(), null, null, true, DataType.TEXT);
    }


    private LocalDateTime localDateTime(String text) {
        try {
            return DateTimeUtil.parse(text.trim());
        } catch (Exception e) {
            return null;
        }

    }
}
