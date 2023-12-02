package com.yishuifengxiao.tool.personalkit.helper.data.impl;

import com.yishuifengxiao.common.tool.io.IoUtil;
import com.yishuifengxiao.tool.personalkit.domain.bo.ExcelRow;
import com.yishuifengxiao.tool.personalkit.domain.enums.DataType;
import com.yishuifengxiao.tool.personalkit.domain.enums.SupportedSuffix;
import com.yishuifengxiao.tool.personalkit.domain.model.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.model.VirtuallyRow;
import com.yishuifengxiao.tool.personalkit.helper.data.BaseFileParser;
import com.yishuifengxiao.tool.personalkit.helper.data.ParserResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 9:56
 * @since 1.0.0
 */
public class CsvFileParser extends BaseFileParser {
    @Override
    public List<ParserResult> execute(File file) {
        try {
            CSVParser csvParser = CSVFormat.DEFAULT.parse(new InputStreamReader(new FileInputStream(file)));
            List<VirtuallyFile.VirtuallyHeader> headers = new ArrayList<>();
            AtomicLong atomic = new AtomicLong(0L);
            List<ExcelRow> rows = new ArrayList<>();
            for (CSVRecord record : csvParser) {

                if (atomic.getAndIncrement() == 0) {
                    //标题
                    for (int columnIndex = 0; columnIndex < record.getRecordNumber(); columnIndex++) {
                        headers.add(new VirtuallyFile.VirtuallyHeader(columnIndex, record.get(columnIndex), DataType.ANY));
                    }
                } else {
                    List<VirtuallyRow.ExcelCell> cells = new ArrayList<>();
                    //标题
                    for (int columnIndex = 0; columnIndex < record.getRecordNumber(); columnIndex++) {
                        cells.add(excelCell(columnIndex, headers, record.get(columnIndex)));
                    }
                    rows.add(new ExcelRow(atomic.get(), cells));
                }
            }
            return Arrays.asList(new ParserResult(file.getName(), headers, rows));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean support(File file) {
        return StringUtils.equalsIgnoreCase(SupportedSuffix.CSV.getSuffix(), IoUtil.suffix(file));
    }
}
