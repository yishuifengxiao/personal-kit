package com.yishuifengxiao.tool.personalkit;

import com.alibaba.fastjson2.JSONObject;
import com.yishuifengxiao.tool.personalkit.helper.data.ParserResult;
import com.yishuifengxiao.tool.personalkit.helper.data.impl.ExcelFileParser;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/27 22:22
 * @since 1.0.0
 */
public class ExcelFileParserTest {

    @Test
    public void test01() {
        List<ParserResult> execute = new ExcelFileParser().execute(new File("D:\\desktop\\住宿记录表.xls"));
        System.out.println(JSONObject.toJSONString(execute));
    }
}
