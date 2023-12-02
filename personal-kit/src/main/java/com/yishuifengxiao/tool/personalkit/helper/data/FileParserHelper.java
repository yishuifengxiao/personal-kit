package com.yishuifengxiao.tool.personalkit.helper.data;

import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.tool.personalkit.domain.enums.DataType;
import com.yishuifengxiao.tool.personalkit.domain.model.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.model.VirtuallyRow;
import com.yishuifengxiao.tool.personalkit.helper.data.impl.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 10:16
 * @since 1.0.0
 */
public class FileParserHelper {

    private final static List<FileParser> list = Arrays.asList(new CsvFileParser(), new ExcelFileParser(), new JsonFileParser(), new JsonLineFileParser(), new TxtFileParser());


    public static List<ParserResult> parse(File file) {
        FileParser parser = list.stream().filter(v -> v.support(file)).findFirst().orElseThrow(() -> new UncheckedException("找不到合适的解析器"));
        List<ParserResult> results = parser.execute(file);

        if (results.isEmpty()) {
            return results;
        }

        return results.parallelStream().map(result -> {

            result.getRows().parallelStream().filter(v -> v.getCells().stream().anyMatch(s -> StringUtils.isNotBlank(s.getText()))).map(v -> v.getCells()).flatMap(Collection::stream).collect(Collectors.groupingBy(VirtuallyRow.ExcelCell::getColumnIndex)).forEach((columnIndex, cellList) -> {
                //按列遍历
                Map<DataType, List<VirtuallyRow.ExcelCell>> groupByType = cellList.stream().filter(v -> !DataType.ANY.equals(v.getDataType())).collect(Collectors.groupingBy(VirtuallyRow.ExcelCell::getDataType));
                DataType columnType = null == groupByType || groupByType.isEmpty() ? DataType.ANY : groupByType.keySet().stream().max(Comparator.comparingInt(v -> groupByType.get(v).size())).orElse(DataType.ANY);

                cellList.stream().map(v -> {
                    v.setIsNormal(columnType == DataType.ANY || columnType.equals(v.getDataType()));
                    return v;
                });
                VirtuallyFile.VirtuallyHeader header = result.getHeaders().stream().filter(v -> v.getColumnIndex().equals(columnIndex)).findFirst().orElse(null);
                if (null != header) {
                    header.setDataType(columnType);
                } else {
                    result.getHeaders().add(new VirtuallyFile.VirtuallyHeader(columnIndex, "_yishui_hidden_column_" + columnIndex, columnType));
                }
            });
            return result;
        }).collect(Collectors.toList());


    }
}
