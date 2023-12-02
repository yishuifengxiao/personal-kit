package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.entity.BaseQuery;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskUploadRecord;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 21:39
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class DataService {


    public Page<DiskUploadRecord> findPageDataRecord(BaseQuery<DiskUploadRecord> pageQuery) {
        return JdbcUtil.jdbcHelper().findPage(pageQuery.query().orElse(new DiskUploadRecord()), pageQuery.size().intValue(), pageQuery.num().intValue()).map(v -> {

            return v;
        });
    }
}
