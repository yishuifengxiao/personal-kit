package com.yishuifengxiao.tool.personalkit.service.esim;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.esim.EsimMon;
import com.yishuifengxiao.tool.personalkit.domain.entity.esim.EsimTempdate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EsimTempdateService {


    public EsimTempdate save(EsimTempdate esimTempdate) {
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimTempdate);
        esimTempdate.setId(keyHolder.getKey().longValue());
        return esimTempdate;
    }


    public List<EsimTempdate> findAll(EsimTempdate esimTempdate) {
        return JdbcUtil.jdbcHelper().findAll(esimTempdate, true);
    }


    public Optional<EsimTempdate> findById(Long id) {
        return Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(EsimTempdate.class, id));
    }


    public void deleteById(Long id) {
        JdbcUtil.jdbcHelper().deleteByPrimaryKey(EsimTempdate.class, id);
    }


    public EsimTempdate update(EsimTempdate esimTempdate) {
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimTempdate);
        esimTempdate.setId(keyHolder.getKey().longValue());
        return esimTempdate;
    }

    public Page<Map> findPage(PageQuery<EsimTempdate> pageQuery) {
        return JdbcUtil.jdbcHelper().findPage(pageQuery, true).map(s -> {
            Map map = BeanUtil.beanToMap(s);
            map.put("monName", Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(EsimMon.class, s.getMonId())).map(EsimMon::getMonShortName).orElse(""));
            return map;
        });
    }
}
