package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.tool.collections.JsonUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.lang.NumberUtil;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.dao.mongo.OntologyDao;
import com.yishuifengxiao.tool.personalkit.dao.mongo.repository.GraphDefineRepository;
import com.yishuifengxiao.tool.personalkit.dao.mongo.repository.OntologyRepository;
import com.yishuifengxiao.tool.personalkit.domain.mongo.Ontology;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import com.yishuifengxiao.tool.personalkit.tool.OntHelper;
import jakarta.transaction.Transactional;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 19:04
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class OntologyService {

    @Autowired
    private OntologyRepository ontologyRepository;
    @Autowired
    private GraphDefineRepository graphDefineRepository;
    @Autowired
    private OntologyDao ontologyDao;

    public Page<Ontology> findPage(PageQuery<Ontology> param) {
        Ontology dataSet = param.query().orElse(new Ontology());
        dataSet.setCreateUserId(ContextCache.currentUserId());
        param.setQuery(dataSet);
        return ontologyDao.findPage(param);
    }

    public String save(Map<String, Object> param) {
        String graphName = MapUtils.getString(param, "graphName");
        Assert.lteZero("已经存在相同名称的本体",
                ontologyRepository.countAllByOntologyNameAndCreateUserId(graphName,
                        ContextCache.currentUserId()));


        Ontology ontology = OntHelper.convert(param).setCreateUserId(ContextCache.currentUserId())
                .setCreateTime(LocalDateTime.now())
                .setVersion(NumberUtil.ZERO.intValue())
                .setMaster(true).setText(JsonUtil.toJSONString(param));


        ontologyRepository.save(ontology);
        return ontology.getId();
    }

    public void update(Map<String, Object> param) {
        String id = MapUtils.getString(param, "id");
        String graphName = MapUtils.getString(param, "graphName");
        Assert.isNotBlank("请选择一条更新记录", id);
        Ontology ontology = ontologyRepository.findById(id).orElseThrow(() -> new UncheckedException(
                "记录不存在"));
        Assert.lteZero("本体正在被使用,不能进行更新", graphDefineRepository.countAllByOntologyId(ontology.getId()));
        if (!StringUtils.equalsIgnoreCase(graphName, ontology.getOntologyName())) {
            throw new UncheckedException("已经存在相同名称的本体");
        }
        Ontology ont = OntHelper.convert(param);
        ont.setCreateUserId(ontology.getCreateUserId())
                .setCreateTime(LocalDateTime.now())
                .setVersion(NumberUtil.ZERO.intValue())
                .setMaster(true);
        ontologyRepository.save(ont);
    }

    public void delete(IdReq param) {
        Assert.lteZero("本体正在被使用,不能删除", graphDefineRepository.countAllByOntologyId(param.getId()));
        ontologyRepository.deleteById(param.getId());
    }

    public Object detail(String id) {
        Ontology ontology = ontologyRepository.findById(id).orElseThrow(() -> UncheckedException.of("记录不存在"));
        return ontology.getText();
    }
}
