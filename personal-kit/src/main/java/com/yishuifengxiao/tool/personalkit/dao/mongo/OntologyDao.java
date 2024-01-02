package com.yishuifengxiao.tool.personalkit.dao.mongo;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.Ontology;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 19:37
 * @since 1.0.0
 */
@Repository
public class OntologyDao {
    @Autowired
    private MongoTemplate mongotemplate;

    public Page<Ontology> findPage(PageQuery<Ontology> param) {
        Ontology ontology = param.query().orElse(new Ontology());
        // 构造查询条件
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(ontology.getOntologyName())) {
            criteria.and("name").regex(Pattern.compile(".*" + ontology.getOntologyName().trim() + ".*"));
        }
        if (StringUtils.isNotBlank(ontology.getCreateUserId())) {
            criteria.and("createUserId").regex(Pattern.compile(".*" + ontology.getCreateUserId().trim() + ".*"));
        }

        // 构造排序方式
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");

        // 构造查询对象
        Query query = new Query(criteria).with(PageRequest.of(param.num().intValue() - 1, param.size().intValue(),
                sort));
        long count = mongotemplate.count(new Query(criteria), Ontology.COLLECTION_NAME);
        List<Ontology> list = mongotemplate.find(query, Ontology.class, Ontology.COLLECTION_NAME);
        return Page.of(list, count, param);
    }
}

