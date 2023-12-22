package com.yishuifengxiao.tool.personalkit.helper.data;

import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyRow;

import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 11:22
 * @since 1.0.0
 */
public abstract class BaseFileParser implements FileParser {

    protected CellHelper cellHelper = new CellHelper();

    protected VirtuallyRow.ExcelCell excelCell(Integer columnIndex, List<VirtuallyFile.VirtuallyHeader> headers, String text) {
       String columnName=
               headers.stream().filter(v -> v.equals(columnIndex)).map(VirtuallyFile.VirtuallyHeader::getName).findFirst().orElse(null);
        return cellHelper.excelCell(columnIndex, columnName, text);
    }
}
