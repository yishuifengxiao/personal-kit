package com.yishuifengxiao.tool.personalkit.domain.vo;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyRow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/12/28 19:41
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtuallyFileVo implements Serializable {

    private List<VirtuallyFile.VirtuallyHeader> headers;

    private Page<VirtuallyRow> page;
}
