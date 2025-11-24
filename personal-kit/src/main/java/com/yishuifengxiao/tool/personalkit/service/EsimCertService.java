package com.yishuifengxiao.tool.personalkit.service;

import com.alibaba.fastjson2.JSONObject;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.codec.ECC;
import com.yishuifengxiao.common.tool.codec.X509Helper;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.lang.HexUtil;
import com.yishuifengxiao.tool.personalkit.domain.entity.EsimCert;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;

@Service
public class EsimCertService {


    public EsimCert save(EsimCert esimCert) {
        EsimCert existCert = JdbcUtil.jdbcHelper().findOne(new EsimCert().setCertName(esimCert.getCertName()), false);
        if (null != existCert) {
            throw new UncheckedException("certName already exists");
        }

        try {
            esimCert = parseEsimCert(esimCert);
            existCert = JdbcUtil.jdbcHelper().findOne(new EsimCert().setCertName(esimCert.getCipkid()), false);
            if (null != existCert) {
                throw new UncheckedException("cipkid already exists");
            }
            KeyHolder keyHolder = JdbcUtil.jdbcHelper().saveOrUpdate(esimCert);
            esimCert.setId(keyHolder.getKey().longValue());

        } catch (Exception e) {
            if (e instanceof UncheckedException) {
                throw (UncheckedException) e;
            }
            e.printStackTrace();
        }
        return esimCert;
    }

    private EsimCert parseEsimCert(EsimCert esimCert) throws CertificateException {
        String extendInfo = JSONObject.toJSONString(esimCert);
        String ciCert = HexUtil.isHex(esimCert.getCiCert()) ? esimCert.getCiCert() : HexUtil.base64ToHex(esimCert.getCiCert());
        X509Certificate ciCertX509Certificate = X509Helper.parseCert(ciCert);
        X509Helper.Cert ciCertFullInfo = X509Helper.extractFullInfo(ciCertX509Certificate);
        if (ciCertFullInfo == null) {
            throw new UncheckedException("ciCert is not a valid x509 certificate");
        }
        if (!ciCertFullInfo.getIsValid()) {
//            throw new UncheckedException("ciCert is not valid");
        }
        X509Certificate ciSubX509Certificate = null;
        // 是否有子集CA证书
        String ciSubCaCert = null;
        if (StringUtils.isNotBlank(esimCert.getCiSubCaCert())) {
            ciSubCaCert = HexUtil.isHex(esimCert.getCiSubCaCert()) ? esimCert.getCiSubCaCert() : HexUtil.base64ToHex(esimCert.getCiSubCaCert());
            ciSubX509Certificate = X509Helper.parseCert(ciSubCaCert);
            X509Helper.Cert ciSubCaCertFullInfo = X509Helper.extractFullInfo(ciSubX509Certificate);
            if (ciSubCaCertFullInfo == null) {
                throw new UncheckedException("ciSubCaCert is not a valid x509 certificate");
            }
            if (!ciSubCaCertFullInfo.getIsValid()) {
//                throw new UncheckedException("ciSubCaCert is not valid");
            }
            boolean issued = X509Helper.isIssuedBy(ciCertX509Certificate, ciSubX509Certificate);
            if (!issued) {
                throw new UncheckedException("ciSubCaCert is not issued by ciCert");
            }
        }
        X509Certificate dpSubCaCertX509Certificate = null;
        String dpSubCaCert = null;
        // 是否有DP子集CA证书
        if (StringUtils.isNotBlank(esimCert.getDpSubCaCert())) {
            dpSubCaCert = HexUtil.isHex(esimCert.getDpSubCaCert()) ? esimCert.getDpSubCaCert() : HexUtil.base64ToHex(esimCert.getDpSubCaCert());
            dpSubCaCertX509Certificate = X509Helper.parseCert(dpSubCaCert);
            X509Helper.Cert dpSubCaCertFullInfo = X509Helper.extractFullInfo(dpSubCaCertX509Certificate);
            if (dpSubCaCertFullInfo == null) {
                throw new UncheckedException("dpSubCaCert is not a valid x509 certificate");
            }
            if (!dpSubCaCertFullInfo.getIsValid()) {
//                throw new UncheckedException("dpSubCaCert is not valid");
            }

            boolean issued = X509Helper.isIssuedBy(ciCertX509Certificate, dpSubCaCertX509Certificate);
            if (!issued) {
                throw new UncheckedException("dpSubCaCert is not issued by ciCert");
            }
        }

        // DP AUTH证书
        String authCert = HexUtil.isHex(esimCert.getAuthCert()) ? esimCert.getAuthCert() : HexUtil.base64ToHex(esimCert.getAuthCert());
        X509Certificate authX509Certificate = X509Helper.parseCert(authCert);
        X509Helper.Cert authCertFullInfo = X509Helper.extractFullInfo(authX509Certificate);
        if (authCertFullInfo == null) {
            throw new UncheckedException("authCert is not a valid x509 certificate");
        }
        if (!authCertFullInfo.getIsValid()) {
//            throw new UncheckedException("authCert is not valid");
        }
        if (null != dpSubCaCertX509Certificate) {
            boolean issued = X509Helper.isIssuedBy(dpSubCaCertX509Certificate, authX509Certificate);
            if (!issued) {
                throw new UncheckedException("authCert is not issued by dpSubCaCert");
            }
        } else {
            boolean issued = X509Helper.isIssuedBy(ciCertX509Certificate, authX509Certificate);
            if (!issued) {
                throw new UncheckedException("authCert is not issued by ciCert");
            }
        }

        String authKey = HexUtil.isHex(esimCert.getAuthKey()) ? esimCert.getAuthKey() : HexUtil.base64ToHex(esimCert.getAuthKey());
        boolean match = ECC.verifyMatch(authCert, authKey);
        if (!match) {
            throw new UncheckedException("authKey is not match with authCert");
        }

        //DP PB证书
        String dbCert = HexUtil.isHex(esimCert.getDbCert()) ? esimCert.getDbCert() : HexUtil.base64ToHex(esimCert.getDbCert());
        X509Certificate dpX509Certificate = X509Helper.parseCert(dbCert);
        X509Helper.Cert dbCertFullInfo = X509Helper.extractFullInfo(dpX509Certificate);
        if (dbCertFullInfo == null) {
            throw new UncheckedException("dbCert is not a valid x509 certificate");
        }
        if (!dbCertFullInfo.getIsValid()) {
//            throw new UncheckedException("dbCert is not valid");
        }
        if (null != dpSubCaCertX509Certificate) {
            boolean issued = X509Helper.isIssuedBy(dpSubCaCertX509Certificate, dpX509Certificate);
            if (!issued) {
                throw new UncheckedException("dpCert is not issued by dpSubCaCert");
            }
        } else {
            boolean issued = X509Helper.isIssuedBy(ciCertX509Certificate, dpX509Certificate);
            if (!issued) {
                throw new UncheckedException("dpCert is not issued by ciCert");
            }
        }
        String dbKey = HexUtil.isHex(esimCert.getDbKey()) ? esimCert.getDbKey() : HexUtil.base64ToHex(esimCert.getDbKey());
        match = ECC.verifyMatch(dbCert, dbKey);
        if (!match) {
            throw new UncheckedException("dbKey is not match with dbCert");
        }


        esimCert.setCiCert(ciCert);
        esimCert.setCiOid(ciCertFullInfo.getOid());
        esimCert.setCipkid(ciCertFullInfo.getCipkid());
        //
        if (StringUtils.isNotBlank(esimCert.getCiSubCaCert())) {
            esimCert.setCiSubCaCert(ciSubCaCert);
        }
        esimCert.setDpSubCaCert(dpSubCaCert);
        esimCert.setAuthCert(authCert);
        esimCert.setAuthKey(authKey);
        esimCert.setDbCert(dbCert);
        esimCert.setDbKey(dbKey);
        esimCert.setExtendInfo(extendInfo);
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
