package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphBuildRecord;
import org.springframework.stereotype.Service;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/18 14:47
 * @since 1.0.0
 */
@Service
public class GraphBuildRecordService {
    public Page<GraphBuildRecord> findPage(PageQuery<GraphBuildRecord> pageQuery) {

        return Page.ofEmpty();
    }
}
