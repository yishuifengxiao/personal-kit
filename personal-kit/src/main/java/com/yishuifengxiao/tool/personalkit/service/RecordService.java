package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysSecurityRecord;
import com.yishuifengxiao.tool.personalkit.domain.request.RecordReq;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/3 18:36
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class RecordService {

    public Page<SysSecurityRecord> findPageSysSecurityRecord(PageQuery<RecordReq> param) {
        String sql = "select * from sys_security_record s and 1=1 ";
        List<Object> params = new ArrayList<>();
        if (null != param.getQuery()) {
            if (null != param.getQuery().getStartTime()) {
                sql += " and create_time >= ? ";
                params.add(param.getQuery().getStartTime());
            }

            if (null != param.getQuery().getEndTime()) {
                sql += " and create_time <= ? ";
                params.add(param.getQuery().getEndTime());
            }

            if (null != param.getQuery().getStrategys()) {
                sql += " and strategy in ? ";
                params.add(param.getQuery().getStrategys());
            }

            if (StringUtils.isNotBlank(param.getQuery().getUsername())) {
                sql += " and username like ? ";
                params.add("%" + param.getQuery().getUsername().trim() + "%");
            }
        }
        sql += "  ORDER BY create_time DESC ";
        return JdbcUtil.jdbcHelper().findPage(SysSecurityRecord.class, param, sql, params.toArray());
    }
}
