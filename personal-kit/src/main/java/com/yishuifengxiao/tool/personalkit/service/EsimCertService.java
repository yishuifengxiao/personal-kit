package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.EsimCert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EsimCertService {


    public EsimCert save(EsimCert esimCert) {
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimCert);
        esimCert.setId(keyHolder.getKeyAs(Long.class));
        return esimCert;
    }


    public List<EsimCert> findAll(EsimCert esimCert) {
        return JdbcUtil.jdbcHelper().findAll(esimCert, true);
    }


    public Optional<EsimCert> findById(Long id) {
        return Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(EsimCert.class, id));
    }


    public void deleteById(Long id) {
        JdbcUtil.jdbcHelper().deleteByPrimaryKey(EsimCert.class, id);
    }


    public EsimCert update(EsimCert esimCert) {
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimCert);
        esimCert.setId(keyHolder.getKeyAs(Long.class));
        return esimCert;
    }

    public Page<EsimCert> findPage(PageQuery<EsimCert> pageQuery) {
        return JdbcUtil.jdbcHelper().findPage(pageQuery, true);
    }
}
