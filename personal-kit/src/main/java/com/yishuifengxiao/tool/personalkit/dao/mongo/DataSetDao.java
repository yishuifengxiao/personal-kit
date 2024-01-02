package com.yishuifengxiao.tool.personalkit.dao.mongo;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.DataSet;
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
 * @date 2024/1/2 18:16
 * @since 1.0.0
 */
@Repository
public class DataSetDao {

    @Autowired
    private MongoTemplate mongotemplate;

    public Page<DataSet> findPage(PageQuery<DataSet> param) {
        DataSet dataSet = param.query().orElse(new DataSet());
        // 构造查询条件
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(dataSet.getName())) {
            criteria.and("name").regex(Pattern.compile(".*" + dataSet.getName().trim() + ".*"));
        }
        if (StringUtils.isNotBlank(dataSet.getCreateUserId())) {
            criteria.and("createUserId").regex(Pattern.compile(".*" + dataSet.getCreateUserId().trim() + ".*"));
        }

        // 构造排序方式
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");

        // 构造查询对象
        Query query = new Query(criteria).with(PageRequest.of(param.num().intValue() - 1, param.size().intValue(),
                sort));
        long count = mongotemplate.count(new Query(criteria), DataSet.COLLECTION_NAME);
        List<DataSet> list = mongotemplate.find(query, DataSet.class, DataSet.COLLECTION_NAME);
        return Page.of(list, count, param);
    }

    public void save(DataSet dataSet) {
        mongotemplate.save(dataSet, DataSet.COLLECTION_NAME);
    }

    public long countByNameAndUser(String name, String user) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(name)) {
            criteria.and("name").is(name.trim());
        }
        if (StringUtils.isNotBlank(user)) {
            criteria.and("createUserId").is(user);
        }

        return mongotemplate.count(new Query(criteria), DataSet.COLLECTION_NAME);
    }


}
