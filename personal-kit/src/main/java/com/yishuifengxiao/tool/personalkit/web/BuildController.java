package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphBuildRecord;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.service.GraphBuildRecordService;
import com.yishuifengxiao.tool.personalkit.service.GraphBuildService;
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

    private final GraphBuildService graphBuildService;

    @PostMapping("/page")
    public Page<GraphBuildRecord> findPage(@RequestBody PageQuery<GraphBuildRecord> pageQuery) {

        return graphBuildRecordService.findPage(pageQuery);
    }

    @PostMapping("/start")
    public void start(@Valid @RequestBody IdReq param) {
        graphBuildService.start(param.getId());
    }

    @PostMapping("/stop")
    public void stop(@Valid @RequestBody IdReq param) {
        graphBuildService.stop(param.getId());
    }
}
