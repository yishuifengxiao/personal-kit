package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import com.yishuifengxiao.tool.personalkit.domain.query.PageQuery;
import org.springframework.stereotype.Component;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/17 15:52
 * @since 1.0.0
 */
@Component
public class SysService {

    public Page<SysPermission> findPagePermission(PageQuery<SysPermission> pageQuery) {
        return JdbcUtil.jdbcHelper().findPage(pageQuery.queryBody().orElse(new SysPermission()), pageQuery.pageSize(),
                pageQuery.pageNum());
    }
}
