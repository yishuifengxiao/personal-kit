package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.Ontology;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.service.OntologyService;
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
@RequestMapping("/graph/ont")
@RequiredArgsConstructor
public class OntologyController {

    @Autowired
    private OntologyService ontologyService;

    @ApiOperation(value = "分页查询数据集", notes = "分页查询数据集")
    @PostMapping("/page")
    public Page<Ontology> findPage(@RequestBody PageQuery<Ontology> pageQuery) {

        return ontologyService.findPage(pageQuery);
    }

    @ApiOperation(value = "增加数据集", notes = "增加数据集")
    @PostMapping("/save")
    public void add(@RequestBody Ontology param) {

        ontologyService.save(param);
    }

    @ApiOperation(value = "更新数据集", notes = "更新数据集")
    @PostMapping("/update")
    public void update(@RequestBody Ontology param) {

        ontologyService.update(param);
    }

    @ApiOperation(value = "删除数据集", notes = "删除数据集")
    @PostMapping("/delete")
    public void delete(@Valid @RequestBody IdReq param) {

        ontologyService.delete(param);
    }

}
