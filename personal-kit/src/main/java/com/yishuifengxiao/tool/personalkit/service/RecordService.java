package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.security.support.Strategy;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysSecurityRecord;
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
}
