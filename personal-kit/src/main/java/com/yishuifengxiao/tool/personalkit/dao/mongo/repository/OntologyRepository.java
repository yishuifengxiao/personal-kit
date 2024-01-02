package com.yishuifengxiao.tool.personalkit.dao.mongo.repository;

import com.yishuifengxiao.tool.personalkit.domain.mongo.Ontology;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 19:04
 * @since 1.0.0
 */
public interface OntologyRepository extends MongoRepository<Ontology, String> {

    long countAllByOntologyNameAndCreateUserId(String name, String userId);
}
