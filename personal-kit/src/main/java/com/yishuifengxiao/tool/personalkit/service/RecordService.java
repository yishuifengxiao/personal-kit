package com.yishuifengxiao.tool.personalkit.service;

import com.alibaba.excel.util.StringUtils;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.security.support.Strategy;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.HttpLog;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysSecurityRecord;
import com.yishuifengxiao.tool.personalkit.domain.entity.UploadRecord;
import com.yishuifengxiao.tool.personalkit.domain.request.HttpLogReq;
import com.yishuifengxiao.tool.personalkit.domain.request.RecordReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.SysSecurityRecordVo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/3 18:36
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class RecordService {

    public Page<SysSecurityRecordVo> findPageSysSecurityRecord(PageQuery<RecordReq> param) {
        RecordReq recordReq = param.query().orElse(new RecordReq());
        SysSecurityRecord record = new SysSecurityRecord();
        return JdbcUtil.jdbcHelper().findPage(record, true, param, Order.desc("createTime"))
                .map(s -> {
                    SysSecurityRecordVo vo = BeanUtil.copy(s, new SysSecurityRecordVo());
                    vo.setStrategyName(Strategy.AUTHENTICATION_SUCCESS.code() == s.getStrategy() ?
                            Strategy.AUTHENTICATION_SUCCESS.enumName() : Strategy.AUTHENTICATION_FAILURE.enumName());
                    return vo;
                });
    }

    public Page<HttpLog> findPageVisitRecord(PageQuery<HttpLogReq> param) {
        HttpLogReq req = param.query().orElse(new HttpLogReq());
        return JdbcUtil.jdbcHelper().find(HttpLog.class, param, map -> {
            String sql = "select * from http_log s where 1=1 ";
            if (StringUtils.isNotBlank(req.getUri())) {
                sql += " and s.uri like concat('%',:uri,'%')";
                map.put("uri", req.getUri());
            }
            if (StringUtils.isNotBlank(req.getMethod())) {
                sql += " and s.method = :method";
                map.put("method", req.getMethod());
            }
            if (StringUtils.isNotBlank(req.getUserId())) {
                sql += " and s.user_id = :userId";
                map.put("userId", req.getUserId());
            }
            if (StringUtils.isNotBlank(req.getUserName())) {
                sql += " and s.user_name like concat('%',:userName,'%')";
                map.put("userName", req.getUserName());
            }
            if (req.getStartTime() != null) {
                sql += " and s.create_time >= :startTime";
                map.put("startTime", req.getStartTime());
            }
            if (req.getEndTime() != null) {
                sql += " and s.create_time <= :endTime";
                map.put("endTime", req.getEndTime());
            }
            sql += " order by s.create_time desc";
            return sql;
        });
    }

    public void clearLoginRecord() {
        JdbcUtil.jdbcHelper().jdbcTemplate().update("DELETE from sys_security_record");
    }

    public void clearVisitRecord() {
        JdbcUtil.jdbcHelper().jdbcTemplate().update("DELETE from http_log");
    }

    public Page<UploadRecord> findPageUploadRecord(PageQuery<UploadRecord> pageQuery) {
        if (null == pageQuery.getQuery()) {
            pageQuery.setQuery(new UploadRecord());
        }
        return JdbcUtil.jdbcHelper().findPage(pageQuery, true, Order.desc("createTime"));

    }
}
