package com.yishuifengxiao.tool.personalkit.service.esim;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.domain.entity.esim.EsimMon;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EsimMonService {


    public EsimMon save(EsimMon esimMon) {
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimMon);
        esimMon.setId(keyHolder.getKey().longValue());
        return esimMon;
    }


    public List<EsimMon> findAll(EsimMon esimMon) {
        return JdbcUtil.jdbcHelper().findAll(esimMon, true);
    }


    public Optional<EsimMon> findById(Long id) {
        return Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(EsimMon.class, id));
    }


    public void deleteById(Long id) {
        EsimMon esimMon = JdbcUtil.jdbcHelper().findByPrimaryKey(EsimMon.class, id);
        Assert.isNotNull("记录不存在", esimMon);
        Assert.isTrue("未禁用的不允许删除", esimMon.getStatus() == 0);
        JdbcUtil.jdbcHelper().deleteByPrimaryKey(EsimMon.class, id);
    }


    public EsimMon update(EsimMon esimMon) {
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimMon);
        esimMon.setId(keyHolder.getKey().longValue());
        return esimMon;
    }

    public Page<EsimMon> findPage(PageQuery<EsimMon> pageQuery) {
        return JdbcUtil.jdbcHelper().findPage(pageQuery, true);
    }
}
