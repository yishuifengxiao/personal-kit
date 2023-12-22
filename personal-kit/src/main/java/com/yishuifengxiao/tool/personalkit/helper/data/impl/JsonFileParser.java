package com.yishuifengxiao.tool.personalkit.helper.data.impl;

import com.alibaba.fastjson2.JSONArray;
import com.yishuifengxiao.common.tool.collections.JsonUtil;
import com.yishuifengxiao.common.tool.io.IoUtil;
import com.yishuifengxiao.tool.personalkit.domain.bo.ExcelRow;
import com.yishuifengxiao.tool.personalkit.domain.enums.DataType;
import com.yishuifengxiao.tool.personalkit.domain.enums.SupportedSuffix;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyRow;
import com.yishuifengxiao.tool.personalkit.helper.data.BaseFileParser;
import com.yishuifengxiao.tool.personalkit.helper.data.ParserResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 9:56
 * @since 1.0.0
 */
@Slf4j
public class JsonFileParser extends BaseFileParser {
    @Override
    public List<ParserResult> execute(File file) {

        try {
            List<VirtuallyFile.VirtuallyHeader> virtuallyHeaders = new ArrayList<>();
            List<ExcelRow> rows = new ArrayList<>();
            JSONArray array = JSONArray.parse(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
            AtomicInteger atomic = new AtomicInteger(0);
            JsonUtil.json2Map(array.getJSONObject(0).toJSONString()).forEach((k, v) -> {
                virtuallyHeaders.add(new VirtuallyFile.VirtuallyHeader(atomic.incrementAndGet(), k, DataType.ANY));
            });


            for (int i = 0; i < array.size(); i++) {
                if (i == 0) {
                    Map<String, Object> rowMap = JsonUtil.json2Map(array.getJSONObject(i).toJSONString());
                    AtomicInteger columnIndex = new AtomicInteger(0);
                    List<VirtuallyRow.ExcelCell> list = rowMap.keySet().stream().map(v -> {
                        int incrementAndGet = columnIndex.incrementAndGet();
                        VirtuallyFile.VirtuallyHeader header = virtuallyHeaders.stream().filter(s -> StringUtils.equals(s.getName(), v)).findFirst().orElse(null);

                        if (null == header) {
                            return null;
                        }
                        Object content = rowMap.get(v);

                        return excelCell(incrementAndGet, virtuallyHeaders, null == content ? null : content.toString());
                    }).filter(Objects::nonNull).collect(Collectors.toList());


                    rows.add(new ExcelRow(i + 1L, list));
                }
            }

            return Arrays.asList(new ParserResult(file.getName(), virtuallyHeaders, rows));

        } catch (Exception e) {
            log.info("--------> 按照jsonArray方式处理文件【{}】时发生问题{}", file.getAbsolutePath(), e);
            throw new RuntimeException("文件不是一个有效的JSON数组文件");
        }
    }

    @Override
    public boolean support(File file) {
        return StringUtils.equalsIgnoreCase(SupportedSuffix.JSON.getSuffix(), IoUtil.suffix(file));
    }
}
