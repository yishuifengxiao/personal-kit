package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.Ontology;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.service.OntologyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 19:02
 * @since 1.0.0
 */
@Tag(name = "本体管理")
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

    @PostMapping(value = "/save", produces = {"application/json;charset=utf-8"})
    public String add(@RequestBody Map<String, Object> param) {


        String saved = ontologyService.save(param);
        return saved;
    }

    @PostMapping("/update")
    public void update(@RequestBody Map<String, Object> param) {

        ontologyService.update(param);
    }

    @PostMapping("/delete")
    public void delete(@Valid @RequestBody IdReq param) {

        ontologyService.delete(param);
    }

    @PostMapping("/detail")
    public Object detail(@Valid @RequestBody IdReq param) {

        return ontologyService.detail(param.getId());
    }

}
