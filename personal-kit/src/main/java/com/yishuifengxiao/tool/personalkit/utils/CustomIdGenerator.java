package com.yishuifengxiao.tool.personalkit.utils;

import com.yishuifengxiao.common.tool.random.IdWorker;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class CustomIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        // 实现您的自定义ID生成逻辑
        // 返回生成的唯一ID

        return IdWorker.snowflakeStringId();
    }
}
