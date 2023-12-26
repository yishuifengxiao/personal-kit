package com.yishuifengxiao.tool.personalkit.helper.data;

import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyRow;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 11:22
 * @since 1.0.0
 */
public abstract class BaseFileParser implements FileParser {

    protected CellHelper cellHelper = new CellHelper();

    protected VirtuallyRow.ExcelCell excelCell(Integer columnIndex, List<VirtuallyFile.VirtuallyHeader> headers, String text) {
        headers = null == headers ? Collections.emptyList() : headers;
        List<String> list = headers.stream().filter(v -> v.getColumnIndex().equals(columnIndex)).filter(Objects::nonNull).map(VirtuallyFile.VirtuallyHeader::getName).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        String columnName = DataUtil.first(list);
        return cellHelper.excelCell(columnIndex, columnName, text);
    }
}
