package com.yishuifengxiao.tool.personalkit.helper.data.impl;

import com.yishuifengxiao.common.tool.collections.JsonUtil;
import com.yishuifengxiao.common.tool.io.IoUtil;
import com.yishuifengxiao.tool.personalkit.domain.bo.ExcelRow;
import com.yishuifengxiao.tool.personalkit.domain.enums.DataType;
import com.yishuifengxiao.tool.personalkit.domain.enums.SupportedSuffix;
import com.yishuifengxiao.tool.personalkit.domain.model.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.model.VirtuallyRow;
import com.yishuifengxiao.tool.personalkit.helper.data.BaseFileParser;
import com.yishuifengxiao.tool.personalkit.helper.data.ParserResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 9:56
 * @since 1.0.0
 */
@Slf4j
public class JsonLineFileParser extends BaseFileParser {
    @Override
    public List<ParserResult> execute(File file) {

        try {
            List<VirtuallyFile.VirtuallyHeader> headers = new ArrayList<>();
            List<ExcelRow> rows = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            AtomicLong rowIndexAtomic = new AtomicLong(0L);


            while ((line = br.readLine()) != null) {
                long rowIndex = rowIndexAtomic.incrementAndGet();
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                Map<String, Object> map = JsonUtil.json2Map(line);
                AtomicInteger columnIndexAtomic = new AtomicInteger(0);
                if (rowIndex == 1) {
                    map.forEach((k, v) -> {
                        headers.add(new VirtuallyFile.VirtuallyHeader(columnIndexAtomic.incrementAndGet(), k, DataType.ANY));
                    });
                }
                columnIndexAtomic.updateAndGet(operand -> 0);

                List<VirtuallyRow.ExcelCell> cells = new ArrayList<>();
                map.forEach((k, v) -> {

                    VirtuallyFile.VirtuallyHeader header = headers.stream().filter(s -> StringUtils.equals(s.getName(), k)).findFirst().orElse(null);
                    if (null == header) {
                        return;
                    }
                    cells.add(excelCell(header.getColumnIndex(), headers, null == v ? null : v.toString()));
                });

                ExcelRow parserRow = new ExcelRow(rowIndex, cells);
                rows.add(parserRow);
            }


            return Arrays.asList(new ParserResult(file.getName(), headers, rows));
        } catch (IOException e) {
            log.info("--------> 按照jsonline方式处理文件【{}】时发生问题{}", file.getAbsolutePath(), e);
            throw new RuntimeException("文件不是一个有效的JSON行文件");
        }

    }

    @Override
    public boolean support(File file) {
        return StringUtils.equalsIgnoreCase(SupportedSuffix.JSON_LINE.getSuffix(), IoUtil.suffix(file));
    }
}
