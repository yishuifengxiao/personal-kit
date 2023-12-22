package com.yishuifengxiao.tool.personalkit.helper.data;

import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.tool.personalkit.domain.bo.ExcelRow;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 9:21
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ParserResult implements Serializable {
    private String sheetName;


    private List<VirtuallyFile.VirtuallyHeader> headers;

    private List<ExcelRow> rows;


    public boolean isEmpty() {
        return CollUtil.isEmpty(this.rows);
    }


}
