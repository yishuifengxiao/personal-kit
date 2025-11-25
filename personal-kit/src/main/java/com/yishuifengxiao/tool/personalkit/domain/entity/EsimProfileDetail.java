package com.yishuifengxiao.tool.personalkit.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * EsimProfile 实体类
 * 对应数据库表: esim_profile
 * 表注释: eSIM Profile完整信息表 - 基于ProfileForm.vue完整数据结构
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "esim_profile_detail") // eSIM Profile完整信息表 - 基于ProfileForm.vue完整数据结构
public class EsimProfileDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID'", updatable = false)
    private Long id;

    @Column(name = "profile_id", nullable = false, columnDefinition = "BIGINT(20) NOT NULL  COMMENT 'esim_profile主键ID'", updatable = false)
    private Long profileId;

    /**
     * V3功能支持：rpmData-RPM数据, deviceSwitchData-设备切换数据, enterpriseProfileData-企业Profile数据
     */
    @Column(name = "v3_features", length = 100, columnDefinition = "VARCHAR(100)  COMMENT 'V3功能支持：rpmData-RPM数据, deviceSwitchData-设备切换数据, enterpriseProfileData-企业Profile数据'")
    private String v3Features;

    /**
     * RPM类型：Enable, Disable, Delete, ListProfileInfo, ContactPcmp
     */
    @Column(name = "rpm_type", length = 100, columnDefinition = "VARCHAR(100) COMMENT 'RPM类型：Enable, Disable, Delete, ListProfileInfo, ContactPcmp'")
    private String rpmType;

    /**
     * RPM下载方式：SM-DP+, SM-DS
     */
    @Column(name = "rpm_download_method", length = 50, columnDefinition = "VARCHAR(50) COMMENT 'RPM下载方式：SM-DP+, SM-DS'")
    private String rpmDownloadMethod;

    /**
     * RPM轮询地址URL
     */
    @Column(name = "rpm_polling_address", length = 200, columnDefinition = "VARCHAR(200) COMMENT 'RPM轮询地址URL'")
    private String rpmPollingAddress;

    /**
     * 允许的CA：CA1, CA2, CA3
     */
    @Column(name = "allowed_ca", length = 10, columnDefinition = "VARCHAR(10) COMMENT '允许的CA：CA1, CA2, CA3'")
    private String allowedCa;

    /**
     * 允许的Tags (JSON数组)
     */
    @Column(name = "allowed_tags", length = 255, columnDefinition = "VARCHAR(255) COMMENT '允许的Tags (JSON数组)'")
    private String allowedTags;

    /**
     * 设备切换方式：requestPlatform-请求平台, useStoredCode-使用存储的激活码
     */
    @Column(name = "device_switch_method", length = 20, columnDefinition = "VARCHAR(20) COMMENT '设备切换方式：requestPlatform-请求平台, useStoredCode-使用存储的激活码'")
    private String deviceSwitchMethod;

    /**
     * 是否需要新设备EID：yes, no
     */
    @Column(name = "need_new_eid", length = 10, columnDefinition = "VARCHAR(10) COMMENT '是否需要新设备EID：yes, no'")
    private String needNewEid;

    /**
     * 是否需要新设备TAC：yes, no
     */
    @Column(name = "need_new_tac", length = 10, columnDefinition = "VARCHAR(10) COMMENT '是否需要新设备TAC：yes, no'")
    private String needNewTac;

    /**
     * 设备切换允许的CA：CA1, CA2, CA3
     */
    @Column(name = "device_switch_allowed_ca", length = 255, columnDefinition = "VARCHAR(255) COMMENT '设备切换允许的CA：CA1, CA2, CA3'")
    private String deviceSwitchAllowedCa;

    /**
     * 企业名称
     */
    @Column(name = "enterprise_name", length = 255, columnDefinition = "VARCHAR(255) COMMENT '企业名称'")
    private String enterpriseName;

    /**
     * 企业规则 (JSON数组)：priorityEnterpriseProfile-优先级企业Profile, onlyInstallEnterpriseProfile-只能安装企业Profile
     */
    @Column(name = "enterprise_rules", columnDefinition = "VARCHAR(255) COMMENT '企业规则 (JSON数组)：priorityEnterpriseProfile-优先级企业Profile, onlyInstallEnterpriseProfile-只能安装企业Profile'")
    private String enterpriseRules;

    /**
     * 非企业Profile数量
     */
    @Column(name = "non_enterprise_profile_count", columnDefinition = "INT(11) DEFAULT '0' COMMENT '非企业Profile数量'")
    private Integer nonEnterpriseProfileCount;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500, columnDefinition = "VARCHAR(500) COMMENT '备注'")
    private String remark;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @Column(name = "del_flag", length = 1, columnDefinition = "CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）'")
    private String delFlag;

    /**
     * 创建者
     */
    @Column(name = "create_by", length = 64, columnDefinition = "VARCHAR(64) COMMENT '创建者'")
    private String createBy;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "DATETIME  COMMENT '创建时间'")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @Column(name = "update_by", length = 64, columnDefinition = "VARCHAR(64) COMMENT '更新者'")
    private String updateBy;

    /**
     * 更新时间
     */
    @Column(name = "update_time", columnDefinition = "DATETIME   COMMENT '更新时间'")
    private LocalDateTime updateTime;

}
