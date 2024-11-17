package com.yishuifengxiao.tool.personalkit.domain.vo;

import com.yishuifengxiao.tool.personalkit.domain.entity.SysSecurityRecord;
import lombok.Data;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2024/11/17 17:21
 * @since 1.0.0
 */
@Data
public class SysSecurityRecordVo extends SysSecurityRecord {

    private String strategyName;
}
