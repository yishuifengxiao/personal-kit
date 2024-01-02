package com.yishuifengxiao.tool.personalkit.dao.mongo.repository;

import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphBuildRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 19:50
 * @since 1.0.0
 */
@Repository
public interface GraphBuildRecordRepository extends MongoRepository<GraphBuildRecord, String> {

    long countByGraphIdAndBuildstat(String graphId, GraphBuildRecord.BuildStat buildstat);

    long countByGraphId(String graphId);
}
