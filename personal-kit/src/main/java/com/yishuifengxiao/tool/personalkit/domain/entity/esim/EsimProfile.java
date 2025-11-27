package com.yishuifengxiao.tool.personalkit.domain.entity.esim;

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
@Table(name = "esim_profile") // eSIM Profile完整信息表 - 基于ProfileForm.vue完整数据结构
public class EsimProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID'", updatable = false)
    private Long id;

    /**
     * ICCID，20位16进制字符串
     */
    @Column(name = "iccid", nullable = false, length = 20, columnDefinition = "VARCHAR(20) NOT NULL COMMENT 'ICCID，20位16进制字符串'")
    private String iccid;

    /**
     * Matching ID，最大长度50
     */
    @Column(name = "matching_id", length = 50, columnDefinition = "VARCHAR(50) COMMENT 'Matching ID，最大长度50'")
    private String matchingId;

    /**
     * EID，32位16进制字符串
     */
    @Column(name = "eid", length = 32, columnDefinition = "VARCHAR(32) COMMENT 'EID，32位16进制字符串'")
    private String eid;

    /**
     * Profile状态：Available, Allocated, Linked, Confirmed, Released, Downloaded, Installed, Error, Unavailable
     */
    @Column(name = "profile_state", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'Available' COMMENT 'Profile状态：Available, Allocated, Linked, Confirmed, Released, Downloaded, Installed, Error, Unavailable'")
    private String profileState;

    /**
     * 通知状态：Enabled, Disabled, Deleted
     */
    @Column(name = "local_profile_state", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'Enabled' COMMENT '通知状态：Enabled, Disabled, Deleted'")
    private String localProfileState;

    /**
     * 下载方式：1-标准下载, 2-快速下载, 3-批量下载, default-默认SM-DP+, activation-激活码, alt-smds-ALT-SM-DS, root-smds-ROOT-SM-DS
     */
    @Column(name = "download_method", length = 20, columnDefinition = "VARCHAR(20) DEFAULT '1' COMMENT '下载方式：1-标准下载, 2-快速下载, 3-批量下载, default-默认SM-DP+, activation-激活码, alt-smds-ALT-SM-DS, root-smds-ROOT-SM-DS'")
    private String downloadMethod;

    /**
     * 所属租户
     */
    @Column(name = "tenant", nullable = false, length = 50, columnDefinition = "VARCHAR(50) NOT NULL COMMENT '所属租户'")
    private String tenant;

    /**
     * 运营商：中国移动, 中国联通, 中国电信, 中国广电
     */
    @Column(name = "mon_oid", length = 20, columnDefinition = "VARCHAR(20) COMMENT '运营商：中国移动, 中国联通, 中国电信, 中国广电'")
    private String monOid;

    /**
     * Profile类别：operational, test, provisioning
     */
    @Column(name = "profile_class", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'operational' COMMENT 'Profile类别：operational, test, provisioning'")
    private String profileClass;

    /**
     * PPR策略：PPR1, PPR2, PPR3
     */
    @Column(name = "ppr_policy", length = 10, columnDefinition = "VARCHAR(10) DEFAULT 'PPR1' COMMENT 'PPR策略：PPR1, PPR2, PPR3'")
    private String pprPolicy;

    /**
     * 重置规则：1-允许重置, 2-禁止重置, 3-条件重置, no_reset-不可重置, resetable-可重置, auto_reset-自动重置, auto_recycle-自动回收
     */
    @Column(name = "reset_rule", length = 20, columnDefinition = "VARCHAR(20) DEFAULT '1' COMMENT '重置规则：1-允许重置, 2-禁止重置, 3-条件重置, no_reset-不可重置, resetable-可重置, auto_reset-自动重置, auto_recycle-自动回收'")
    private String resetRule;

    /**
     * DS标识：flag1, flag2, flag3
     */
    @Column(name = "ds_flag", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'flag1' COMMENT 'DS标识：flag1, flag2, flag3'")
    private String dsFlag;

    /**
     * 确认码
     */
    @Column(name = "confirmation_code", length = 50, columnDefinition = "VARCHAR(50) COMMENT '确认码'")
    private String confirmationCode;

    /**
     * Profile名称
     */
    @Column(name = "profile_name", length = 100, columnDefinition = "VARCHAR(100) COMMENT 'Profile名称'")
    private String profileName;

    /**
     * 电话号码
     */
    @Column(name = "phone_number", length = 100, columnDefinition = "VARCHAR(100) COMMENT '电话号码'")
    private String phoneNumber;

    /**
     * 服务提供商
     */
    @Column(name = "service_provider", length = 50, columnDefinition = "VARCHAR(50) COMMENT '服务提供商'")
    private String serviceProvider;

    /**
     * 通知事件
     */
    @Column(name = "notification_event", length = 50, columnDefinition = "VARCHAR(50) COMMENT '通知事件'")
    private String notificationEvent;

    /**
     * 通知地址URL
     */
    @Column(name = "notification_address", length = 200, columnDefinition = "VARCHAR(200) COMMENT '通知地址URL'")
    private String notificationAddress;

    /**
     * Profile图标
     */
    @Column(name = "profile_icon", length = 50, columnDefinition = "VARCHAR(50) COMMENT 'Profile图标'")
    private String profileIcon;

    /**
     * IMSI，15位数字
     */
    @Column(name = "imsi", length = 15, columnDefinition = "VARCHAR(15) COMMENT 'IMSI，15位数字'")
    private String imsi;

    /**
     * IMSI2，15位数字
     */
    @Column(name = "imsi2", length = 15, columnDefinition = "VARCHAR(15) COMMENT 'IMSI2，15位数字'")
    private String imsi2;

    /**
     * PIN1
     */
    @Column(name = "pin1", length = 10, columnDefinition = "VARCHAR(10) COMMENT 'PIN1'")
    private String pin1;

    /**
     * PIN2
     */
    @Column(name = "pin2", length = 10, columnDefinition = "VARCHAR(10) COMMENT 'PIN2'")
    private String pin2;

    /**
     * PUK1
     */
    @Column(name = "puk1", length = 10, columnDefinition = "VARCHAR(10) COMMENT 'PUK1'")
    private String puk1;

    /**
     * PUK2
     */
    @Column(name = "puk2", length = 10, columnDefinition = "VARCHAR(10) COMMENT 'PUK2'")
    private String puk2;

    /**
     * ADM1
     */
    @Column(name = "adm1", nullable = false, length = 50, columnDefinition = "VARCHAR(50) NOT NULL COMMENT 'ADM1'")
    private String adm1;

    /**
     * KI密钥
     */
    @Column(name = "ki", length = 50, columnDefinition = "VARCHAR(50) COMMENT 'KI密钥'")
    private String ki;

    /**
     * OPC密钥
     */
    @Column(name = "opc", length = 50, columnDefinition = "VARCHAR(50) COMMENT 'OPC密钥'")
    private String opc;

    /**
     * SMSP
     */
    @Column(name = "smsp", length = 50, columnDefinition = "VARCHAR(50) COMMENT 'SMSP'")
    private String smsp;


    /**
     * ASN模板：template1-标准模版, template2-企业模版, template3-自定义模版
     */
    @Column(name = "asn_template", length = 50, columnDefinition = "VARCHAR(50) COMMENT 'ASN模板：template1-标准模版, template2-企业模版, template3-自定义模版'")
    private String asnTemplate;


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
    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    private LocalDateTime updateTime;

    @Column(name = "confirm_limit", columnDefinition = "INT COMMENT '确认限制'")
    private Integer confirmLimit;
}
