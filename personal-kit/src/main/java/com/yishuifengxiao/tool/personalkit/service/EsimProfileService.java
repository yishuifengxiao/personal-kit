package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.EsimProfile;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EsimProfileService {
    
     
    public EsimProfile save(EsimProfile esimProfile) {
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimProfile);
        esimProfile.setId(keyHolder.getKey().longValue());
        return esimProfile;
    }
    
     
    public List<EsimProfile> findAll(EsimProfile esimProfile) {
        return JdbcUtil.jdbcHelper().findAll(esimProfile, true);
    }
    
     
    public Optional<EsimProfile> findById(Long id) {
        return Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(EsimProfile.class, id));
    }
    
     
    public void deleteById(Long id) {
        JdbcUtil.jdbcHelper().deleteByPrimaryKey(EsimProfile.class, id);
    }
    
     
    public EsimProfile update(EsimProfile esimProfile) {
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimProfile);
        esimProfile.setId(keyHolder.getKey().longValue());
        return esimProfile;
    }

    public Page<EsimProfile> findPage(PageQuery<EsimProfile> pageQuery) {
        return JdbcUtil.jdbcHelper().findPage(pageQuery, true);
    }
}
