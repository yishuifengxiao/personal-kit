package com.yishuifengxiao.tool.personalkit.helper.data.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ConverterUtils;
import com.yishuifengxiao.common.tool.io.IoUtil;
import com.yishuifengxiao.tool.personalkit.domain.bo.ExcelRow;
import com.yishuifengxiao.tool.personalkit.domain.enums.DataType;
import com.yishuifengxiao.tool.personalkit.domain.enums.SupportedSuffix;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyRow;
import com.yishuifengxiao.tool.personalkit.helper.data.BaseFileParser;
import com.yishuifengxiao.tool.personalkit.helper.data.ParserResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 9:57
 * @since 1.0.0
 */
@Slf4j
public class ExcelFileParser extends BaseFileParser {
    @Override
    public List<ParserResult> execute(File file) {

        List<ParserResult> list = new ArrayList<>();
        try {
            List<ReadSheet> readSheets = EasyExcel.read(file).build().excelExecutor().sheetList();

            readSheets.parallelStream().forEach(sheet -> {
                EasyExcel.read(file, new AnalysisEventListener<Map<Integer, String>>() {
                    ParserResult parserResult = new ParserResult().setSheetName(sheet.getSheetName());
                    List<ExcelRow> rows = new ArrayList<>();
                    AtomicLong atomic = new AtomicLong(0L);

                    /**
                     * 这里会一行行的返回头
                     *
                     * @param headMap
                     * @param context
                     */
                    @Override
                    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {

                        // 如果想转成成 Map<Integer,String>
                        // 方案1： 不要implements ReadListener 而是 extends AnalysisEventListener
                        // 方案2： 调用 ConverterUtils.convertToStringMap(headMap, context) 自动会转换

                        Map<Integer, String> headers = ConverterUtils.convertToStringMap(headMap, context);
                        List<VirtuallyFile.VirtuallyHeader> virtuallyHeaders = headers.keySet().stream().map(h -> new VirtuallyFile.VirtuallyHeader(h, headers.get(h), DataType.ANY)).collect(Collectors.toList());
                        parserResult.setHeaders(virtuallyHeaders);
                    }

                    @Override
                    public void invoke(Map<Integer, String> integerStringMap, AnalysisContext analysisContext) {
                        List<VirtuallyRow.ExcelCell> cells = new ArrayList<>();

                        integerStringMap.forEach((k, v) -> {
                            if (k <= parserResult.getHeaders().size()) {
                                // 以表头为准
                                cells.add(excelCell(k, parserResult.getHeaders(), v));
                            }

                        });

                        rows.add(new ExcelRow(atomic.incrementAndGet(), cells));
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                        parserResult.setRows(rows);
                        list.add(parserResult);
                    }
                }).sheet(sheet.getSheetName()).doRead();

            });
        } catch (Exception e) {
            log.info("--------> 按照EXCEL方式处理文件【{}】时发生问题{}", file.getAbsolutePath(), e);
            throw new RuntimeException("文件不是一个有效的EXCEL文件");
        }

        return list;
    }

    @Override
    public boolean support(File file) {
        return StringUtils.equalsIgnoreCase(SupportedSuffix.EXCEL_XLS.getSuffix(), IoUtil.suffix(file)) || StringUtils.equalsIgnoreCase(SupportedSuffix.EXCEL_XLSX.getSuffix(), IoUtil.suffix(file));
    }
}
