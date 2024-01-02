package com.yishuifengxiao.tool.personalkit.dao.mongo.repository;

import com.yishuifengxiao.tool.personalkit.domain.mongo.DataSet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSetRepository extends MongoRepository<DataSet, String> {
}