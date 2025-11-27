package com.yishuifengxiao.tool.personalkit.service.esim;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.esim.EsimMon;
import com.yishuifengxiao.tool.personalkit.domain.entity.esim.EsimTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EsimTempdateService {


    public EsimTemplate save(EsimTemplate esimTemplate) {
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimTemplate);
        esimTemplate.setId(keyHolder.getKey().longValue());
        return esimTemplate;
    }


    public List<EsimTemplate> findAll(EsimTemplate esimTemplate) {
        return JdbcUtil.jdbcHelper().findAll(esimTemplate, true);
    }


    public Optional<EsimTemplate> findById(Long id) {
        return Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(EsimTemplate.class, id));
    }


    public void deleteById(Long id) {
        JdbcUtil.jdbcHelper().deleteByPrimaryKey(EsimTemplate.class, id);
    }


    public EsimTemplate update(EsimTemplate esimTemplate) {
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimTemplate);
        esimTemplate.setId(keyHolder.getKey().longValue());
        return esimTemplate;
    }

    public Page<Map> findPage(PageQuery<EsimTemplate> pageQuery) {
        return JdbcUtil.jdbcHelper().findPage(pageQuery, true).map(s -> {
            Map map = BeanUtil.beanToMap(s);
            map.put("monName", Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(EsimMon.class, s.getMonId())).map(EsimMon::getMonShortName).orElse(""));
            return map;
        });
    }
}
