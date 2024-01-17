package com.yishuifengxiao.tool.personalkit.dao.mongo.repository;

import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/17 17:06
 * @since 1.0.0
 */
@Repository
public interface VirtuallyFileRepository extends MongoRepository<VirtuallyFile, String> {

}
