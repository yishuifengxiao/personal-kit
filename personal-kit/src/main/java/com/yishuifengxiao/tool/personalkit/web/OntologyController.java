package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.bo.GraphData;
import com.yishuifengxiao.tool.personalkit.domain.mongo.Ontology;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.service.OntologyService;
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
@RequestMapping("/graph/ont")
@RequiredArgsConstructor
public class OntologyController {

    @Autowired
    private OntologyService ontologyService;

    @PostMapping("/page")
    public Page<Ontology> findPage(@RequestBody PageQuery<Ontology> pageQuery) {

        return ontologyService.findPage(pageQuery);
    }

    @PostMapping("/save")
    public void add(@RequestBody GraphData param) {


        ontologyService.save(param);
    }

    @PostMapping("/update")
    public void update(@RequestBody GraphData param) {

        ontologyService.update(param);
    }

    @PostMapping("/delete")
    public void delete(@Valid @RequestBody IdReq param) {

        ontologyService.delete(param);
    }

}
