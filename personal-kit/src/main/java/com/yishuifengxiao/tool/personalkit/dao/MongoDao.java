package com.yishuifengxiao.tool.personalkit.dao;

import com.mongodb.client.MongoClient;
import com.yishuifengxiao.tool.personalkit.domain.model.VirtuallyFile;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 22:55
 * @since 1.0.0
 */
@Component
public class MongoDao {
    @Autowired
    private MongoTemplate mongotemplate;

    @Autowired
    private MongoClient mongoClient;

    public VirtuallyFile findVirtuallyFileByFileId(String fileId) {
        Query query = new Query();
        query.addCriteria(new Criteria("fileId").is(fileId));
        return mongotemplate.find(query, VirtuallyFile.class).stream().findFirst().orElse(null);
    }

    public Long findMaxRowIndexByVirtuallyFileId(String virtuallyFileId) {
        // 构造查询条件
        Criteria criteria = Criteria.where("virtuallyFileId").is(virtuallyFileId);

        // 构造排序方式
        Sort sort = Sort.by(Sort.Direction.DESC, "rowIndex.$numberLong");

        // 构造查询对象
        Query query = new Query(criteria).with(sort).limit(1);

        // 执行查询
        Document result = mongotemplate.findOne(query, Document.class, "virtuallyRow");

        // 处理查询结果
        if (result != null) {
            Long maxRowIndex = result.getLong("rowIndex");
            return maxRowIndex;
        }

        return null;
    }

    public Long countByVirtuallyFileId(String virtuallyFileId) {
        // 构造查询条件
        Criteria criteria = Criteria.where("virtuallyFileId").is(virtuallyFileId);

        // 构造排序方式

        // 构造查询对象
        Query query = new Query(criteria);


        return mongotemplate.count(query, "virtuallyRow");
    }
}