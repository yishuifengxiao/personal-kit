package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphDefine;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.service.GraphService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
    public Page<GraphDefine> findPage(@RequestBody PageQuery<GraphDefine> pageQuery) {

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

}
