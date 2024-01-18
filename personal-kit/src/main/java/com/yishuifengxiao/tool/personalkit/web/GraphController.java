package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphDefine;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.GraphDefineVo;
import com.yishuifengxiao.tool.personalkit.service.GraphService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 19:02
 * @since 1.0.0
 */
@Valid
@RestController
@RequestMapping("/graph/define")
@RequiredArgsConstructor
public class GraphController {

    @Autowired
    private GraphService graphService;

    @PostMapping("/page")
    public Page<GraphDefineVo> findPage(@RequestBody PageQuery<GraphDefine> pageQuery) {

        return graphService.findPage(pageQuery);
    }

    @PostMapping("/save")
    public void add(@RequestBody GraphDefine param) {

        graphService.save(param);
    }

    @PostMapping("/update")
    public void update(@RequestBody GraphDefine param) {

        graphService.update(param);
    }

    @PostMapping("/delete")
    public void delete(@Valid @RequestBody IdReq param) {

        graphService.delete(param);
    }

    @PostMapping("/detail")
    public GraphDefine detail(@Valid @RequestBody IdReq param) {

        return graphService.detail(param);
    }

    @PostMapping("/updateDataSource")
    public void updateDataSource(@Valid @RequestBody GraphDefine.DataSource dataSource) {
        graphService.updateDataSource(dataSource);
    }

    @PostMapping("/dataSource")
    public GraphDefine.DataSource dataSource(@Valid @RequestBody IdReq param) {
        return graphService.dataSource(param.getId());
    }

    @PostMapping("/updateNodeMapping")
    public void updateNodeMapping(@Valid @RequestBody GraphDefine.NodeMapping nodeMapping) {
        graphService.updateNodeMapping(nodeMapping);
    }

    @PostMapping("/nodeMapping")
    public List<GraphDefine.NodeMapping> nodeMapping(@Valid @RequestBody IdReq param) {
        return graphService.nodeMapping(param.getId());
    }


}
