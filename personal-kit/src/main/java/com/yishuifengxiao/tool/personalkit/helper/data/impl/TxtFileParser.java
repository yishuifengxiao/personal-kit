package com.yishuifengxiao.tool.personalkit.helper.data.impl;

import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.io.IoUtil;
import com.yishuifengxiao.tool.personalkit.domain.bo.ExcelRow;
import com.yishuifengxiao.tool.personalkit.domain.enums.DataType;
import com.yishuifengxiao.tool.personalkit.domain.enums.SupportedSuffix;
import com.yishuifengxiao.tool.personalkit.domain.model.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.helper.data.BaseFileParser;
import com.yishuifengxiao.tool.personalkit.helper.data.ParserResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 9:29
 * @since 1.0.0
 */
@Slf4j
public class TxtFileParser extends BaseFileParser {

    @Override
    public List<ParserResult> execute(File file) {
        try {
            List<ExcelRow> rows = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            AtomicLong atomic = new AtomicLong(0L);
            List<VirtuallyFile.VirtuallyHeader> headers = DataUtil.asList(new VirtuallyFile.VirtuallyHeader(0, SupportedSuffix.TXT.getSuffix(), DataType.ANY));
            while ((line = br.readLine()) != null) {
                long rowIndex = atomic.incrementAndGet();
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                ExcelRow parserRow = new ExcelRow(rowIndex, DataUtil.asList(excelCell(0, headers, line)));
                rows.add(parserRow);
            }
            return Arrays.asList(new ParserResult(file.getName(), headers, rows));
        } catch (IOException e) {
            log.info("--------> 按照csv方式处理文件【{}】时发生问题{}", file.getAbsolutePath(), e);
            throw new RuntimeException("文件不是一个有效的TXT文件");
        }


    }

    @Override
    public boolean support(File file) {
        return StringUtils.equalsIgnoreCase("txt", IoUtil.suffix(file));
    }
}
