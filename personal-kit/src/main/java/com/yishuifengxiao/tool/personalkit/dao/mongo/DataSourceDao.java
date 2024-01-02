package com.yishuifengxiao.tool.personalkit.dao.mongo;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyRow;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 22:55
 * @since 1.0.0
 */
@Repository
public class DataSourceDao {
    @Autowired
    private MongoTemplate mongotemplate;

    public List<VirtuallyFile> findVirtuallyFileByFileId(String fileId) {
        Query query = new Query();
        query.addCriteria(new Criteria("fileId").is(fileId));
        return mongotemplate.find(query, VirtuallyFile.class, VirtuallyFile.COLLECTION_NAME);
    }

    public Long findMaxRowIndexByVirtuallyFileId(String virtuallyFileId) {
        // 构造查询条件
        Criteria criteria = Criteria.where("virtuallyFileId").is(virtuallyFileId);

        // 构造排序方式
        Sort sort = Sort.by(Sort.Direction.DESC, "rowIndex");

        // 构造查询对象
        Query query = new Query(criteria).with(sort).limit(1);

        // 执行查询
        Document result = mongotemplate.findOne(query, Document.class, VirtuallyRow.COLLECTION_NAME);

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


        return mongotemplate.count(query, VirtuallyRow.COLLECTION_NAME);
    }

    public VirtuallyFile findVirtuallyFileById(String id) {
        return mongotemplate.findById(id, VirtuallyFile.class, VirtuallyFile.COLLECTION_NAME);
    }

    public Page<VirtuallyRow> findPageVirtuallyRow(PageQuery<VirtuallyRow> pageQuery) {
        VirtuallyRow virtuallyRow = pageQuery.query().orElse(new VirtuallyRow());
        // 构造查询条件
        Criteria criteria = Criteria.where("virtuallyFileId").is(virtuallyRow.getVirtuallyFileId());

        // 构造排序方式
        Sort sort = Sort.by(Sort.Direction.ASC, "rowIndex");

        // 构造查询对象
        Query query =
                new Query(criteria).with(sort).skip(pageQuery.startOffset().longValue()).limit(pageQuery.size().intValue());
        long count = mongotemplate.count(new Query(criteria), VirtuallyRow.COLLECTION_NAME);
        List<VirtuallyRow> rows = mongotemplate.find(query, VirtuallyRow.class, VirtuallyRow.COLLECTION_NAME);
        return Page.of(rows, count, pageQuery);
    }
}
