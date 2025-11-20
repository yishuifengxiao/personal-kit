package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.codec.ECC;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.lang.HexUtil;
import com.yishuifengxiao.tool.personalkit.domain.entity.EsimCert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EsimCertService {


    public EsimCert save(EsimCert esimCert) {
        esimCert = parseEsimCert(esimCert);
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimCert);
        esimCert.setId(keyHolder.getKey().longValue());
        return esimCert;
    }

    private EsimCert parseEsimCert(EsimCert esimCert) {
        String ciCert = HexUtil.isHex(esimCert.getCiCert()) ? esimCert.getCiCert() : HexUtil.base64ToHex(esimCert.getCiCert());
        ECC.convertPrivateKeyToHex()
        String ciSubCaCert = HexUtil.isHex(esimCert.getCiSubCaCert()) ? esimCert.getCiSubCaCert() : HexUtil.base64ToHex(esimCert.getCiSubCaCert());
        String dpSubCaCert = HexUtil.isHex(esimCert.getDpSubCaCert()) ? esimCert.getDpSubCaCert() : HexUtil.base64ToHex(esimCert.getDpSubCaCert());
        String authCert = HexUtil.isHex(esimCert.getAuthCert()) ? esimCert.getAuthCert() : HexUtil.base64ToHex(esimCert.getAuthCert());
        String authKey = HexUtil.isHex(esimCert.getAuthKey()) ? esimCert.getAuthKey() : HexUtil.base64ToHex(esimCert.getAuthKey());
        String dbCert = HexUtil.isHex(esimCert.getDbCert()) ? esimCert.getDbCert() : HexUtil.base64ToHex(esimCert.getDbCert());
        String dbKey = HexUtil.isHex(esimCert.getDbKey()) ? esimCert.getDbKey() : HexUtil.base64ToHex(esimCert.getDbKey());
        esimCert.setDpSubCaCert(dpSubCaCert);
        esimCert.setCiSubCaCert(ciSubCaCert);
        esimCert.setAuthKey(authKey);
        esimCert.setDbKey(dbKey);
        esimCert.setDbCert(dbCert);
        esimCert.setAuthCert(authCert);

        esimCert.setCiCert(ciCert);
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
        esimCert.setId(keyHolder.getKey().longValue());
        return esimCert;
    }

    public Page<EsimCert> findPage(PageQuery<EsimCert> pageQuery) {
        return JdbcUtil.jdbcHelper().findPage(pageQuery, true);
    }
}
