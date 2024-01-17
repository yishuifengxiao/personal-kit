package com.yishuifengxiao.tool.personalkit.dao.mongo;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphDefine;
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
 * @date 2024/1/2 19:28
 * @since 1.0.0
 */
@Repository
public class GraphDefineDao {
    @Autowired
    private MongoTemplate mongotemplate;

    public Page<GraphDefine> findPage(PageQuery<GraphDefine> param) {
        GraphDefine graphDefine = param.query().orElse(new GraphDefine());
        // 构造查询条件
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(graphDefine.getGraphName())) {
            criteria.and("name").regex(Pattern.compile(".*" + graphDefine.getGraphName().trim() + ".*"));
        }
        if (StringUtils.isNotBlank(graphDefine.getCreateUserId())) {
            criteria.and("createUserId").regex(Pattern.compile(".*" + graphDefine.getCreateUserId().trim() + ".*"));
        }

        // 构造排序方式
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");

        // 构造查询对象
        Query query = new Query(criteria).with(PageRequest.of(param.num().intValue() - 1, param.size().intValue(),
                sort));
        long count = mongotemplate.count(new Query(criteria), GraphDefine.COLLECTION_NAME);
        List<GraphDefine> list = mongotemplate.find(query, GraphDefine.class, GraphDefine.COLLECTION_NAME);
        return Page.of(list, count, param);
    }

    public long countByOntId(String ontId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("ontologyId").is(ontId));
        return mongotemplate.count(query, GraphDefine.COLLECTION_NAME);
    }
}
