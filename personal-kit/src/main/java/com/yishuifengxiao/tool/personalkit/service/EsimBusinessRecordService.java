package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.EsimBusinessRecord;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EsimBusinessRecordService {

    public EsimBusinessRecord save(EsimBusinessRecord esimBusinessRecord) {
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimBusinessRecord);
        esimBusinessRecord.setId(keyHolder.getKey().longValue());
        return esimBusinessRecord;
    }

    public List<EsimBusinessRecord> findAll(EsimBusinessRecord esimBusinessRecord) {
        return JdbcUtil.jdbcHelper().findAll(esimBusinessRecord, true);
    }

    public Optional<EsimBusinessRecord> findById(String id) {
        return Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(EsimBusinessRecord.class, id));
    }

    public void deleteById(Long id) {
        JdbcUtil.jdbcHelper().deleteByPrimaryKey(EsimBusinessRecord.class, id);
    }

    public EsimBusinessRecord update(EsimBusinessRecord esimBusinessRecord) {
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimBusinessRecord);
        esimBusinessRecord.setId(keyHolder.getKey().longValue());
        return esimBusinessRecord;
    }

    public Page<EsimBusinessRecord> findPage(PageQuery<EsimBusinessRecord> pageQuery) {
        return JdbcUtil.jdbcHelper().findPage(pageQuery, true);
    }
}
