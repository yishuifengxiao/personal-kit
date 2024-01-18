package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphBuildRecord;
import com.yishuifengxiao.tool.personalkit.service.GraphBuildRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/18 14:44
 * @since 1.0.0
 */
@Valid
@RestController
@RequestMapping("/graph/build")
@RequiredArgsConstructor
public class BuildController {
    private final GraphBuildRecordService graphBuildRecordService;

    @PostMapping("/page")
    public Page<GraphBuildRecord> findPage(@RequestBody PageQuery<GraphBuildRecord> pageQuery) {

        return graphBuildRecordService.findPage(pageQuery);
    }
}
