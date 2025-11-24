package com.yishuifengxiao.tool.personalkit.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * EsimCert 实体类
 * 对应数据库表: esim_cert
 * 表注释: 证书管理表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsimCertExcel implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 证书名称
     */
    private String certName;

    /**
     * CI证书内容
     */
    private String ciCert;


    /**
     * CI SubCA证书内容
     */
    private String ciSubCaCert;

    /**
     * DP SubCA证书内容
     */
    private String dpSubCaCert;

    /**
     * 认证证书内容
     */
    private String authCert;

    /**
     * 认证私钥/标签
     */
    private String authKey;


    /**
     * 数据绑定证书内容
     */
    private String dbCert;

    /**
     * 数据绑定私钥/标签
     */
    private String dbKey;

    /**
     * 是否标签 1-标签 0-私钥
     */
    private Boolean isLabel;


}
