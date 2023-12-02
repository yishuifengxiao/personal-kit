package com.yishuifengxiao.tool.personalkit.helper.data;

import java.io.File;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 9:18
 * @since 1.0.0
 */
public interface FileParser {

    List<ParserResult> execute(File file);


    boolean support(File file);

}
