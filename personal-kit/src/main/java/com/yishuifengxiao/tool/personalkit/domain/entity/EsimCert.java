package com.yishuifengxiao.tool.personalkit.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * EsimCert 实体类
 * 对应数据库表: esim_cert
 * 表注释: 证书管理表
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "esim_cert") // 证书管理表
public class EsimCert implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID'", updatable = false)
    private Long id;

    /**
     * 证书名称
     */
    @Column(name = "cert_name", nullable = false, length = 100, columnDefinition = "VARCHAR(100) NOT NULL COMMENT '证书名称'")
    private String certName;

    /**
     * CI证书内容
     */
    @Column(name = "ci_cert", columnDefinition = "TEXT COMMENT 'CI证书内容'")
    private String ciCert;

    /**
     * CI SubCA证书内容
     */
    @Column(name = "ci_sub_ca_cert", columnDefinition = "TEXT COMMENT 'CI SubCA证书内容'")
    private String ciSubCaCert;

    /**
     * DP SubCA证书内容
     */
    @Column(name = "dp_sub_ca_cert", columnDefinition = "TEXT COMMENT 'DP SubCA证书内容'")
    private String dpSubCaCert;

    /**
     * 认证证书内容
     */
    @Column(name = "auth_cert", nullable = false, columnDefinition = "TEXT NOT NULL COMMENT '认证证书内容'")
    private String authCert;

    /**
     * 认证私钥/标签
     */
    @Column(name = "auth_key", nullable = false, columnDefinition = "TEXT NOT NULL COMMENT '认证私钥/标签'")
    private String authKey;

    /**
     * 数据绑定证书内容
     */
    @Column(name = "db_cert", nullable = false, columnDefinition = "TEXT NOT NULL COMMENT '数据绑定证书内容'")
    private String dbCert;

    /**
     * 数据绑定私钥/标签
     */
    @Column(name = "db_key", nullable = false, columnDefinition = "TEXT NOT NULL COMMENT '数据绑定私钥/标签'")
    private String dbKey;

    /**
     * 是否标签 1-标签 0-私钥
     */
    @Column(name = "is_label", columnDefinition = "BIT DEFAULT '0' COMMENT '是否标签 1-标签 0-私钥'")
    private Boolean isLabel;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "DATETIME DEFAULT 'CURRENT_TIMESTAMP' COMMENT '创建时间'")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time", columnDefinition = "DATETIME DEFAULT 'CURRENT_TIMESTAMP' COMMENT '更新时间'")
    private LocalDateTime updateTime;

    /**
     * CI证书的CIPKID
     */
    @Column(name = "cipkid", length = 100, columnDefinition = "VARCHAR(100) COMMENT 'CI证书的CIPKID'")
    private String cipkid;

}
