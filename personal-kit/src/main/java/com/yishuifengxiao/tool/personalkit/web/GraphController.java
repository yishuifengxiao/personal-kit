package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphDefine;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.service.GraphService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 19:02
 * @since 1.0.0
 */
@Api(tags = {"菜单接口"})
@Valid
@RestController
@RequestMapping("/graph/define")
@RequiredArgsConstructor
public class GraphController {

    @Autowired
    private GraphService graphService;

    @ApiOperation(value = "分页查询图谱", notes = "分页查询图谱")
    @PostMapping("/page")
    public Page<GraphDefine> findPage(@RequestBody PageQuery<GraphDefine> pageQuery) {

        return graphService.findPage(pageQuery);
    }

    @ApiOperation(value = "增加图谱", notes = "增加图谱")
    @PostMapping("/save")
    public void add(@RequestBody GraphDefine param) {

        graphService.save(param);
    }

    @ApiOperation(value = "更新图谱", notes = "更新图谱")
    @PostMapping("/update")
    public void update(@RequestBody GraphDefine param) {

        graphService.update(param);
    }

    @ApiOperation(value = "删除图谱", notes = "删除图谱")
    @PostMapping("/delete")
    public void delete(@Valid @RequestBody IdReq param) {

        graphService.delete(param);
    }

}
