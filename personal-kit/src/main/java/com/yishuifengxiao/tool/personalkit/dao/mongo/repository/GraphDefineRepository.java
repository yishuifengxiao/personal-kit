package com.yishuifengxiao.tool.personalkit.dao.mongo.repository;

import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphDefine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 19:25
 * @since 1.0.0
 */
@Repository
public interface GraphDefineRepository extends MongoRepository<GraphDefine, String> {



    long countAllByOntologyId(String ontId);

    long countAllByGraphNameAndCreateUserId(String name, String userId);
}
