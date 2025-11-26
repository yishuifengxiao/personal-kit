package com.yishuifengxiao.tool.personalkit.domain.entity.esim;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * EsimBusinessRecord 实体类
 * 对应数据库表: esim_business_record
 * 表注释: 业务记录表
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "esim_business_record") // 业务记录表
public class EsimBusinessRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID'", updatable = false)
    private Long id;

    /**
     * ICCID
     */
    @Column(name = "iccid", length = 20, columnDefinition = "VARCHAR(20) COMMENT 'ICCID'")
    private String iccid;

    /**
     * EID
     */
    @Column(name = "eid", length = 32, columnDefinition = "VARCHAR(32) COMMENT 'EID'")
    private String eid;

    /**
     * 匹配ID
     */
    @Column(name = "match_id", length = 50, columnDefinition = "VARCHAR(50) COMMENT '匹配ID'")
    private String matchId;

    /**
     * 功能名称
     */
    @Column(name = "function_name", length = 100, columnDefinition = "VARCHAR(100) COMMENT '功能名称'")
    private String functionName;

    /**
     * 请求数据
     */
    @Column(name = "request_data", columnDefinition = "TEXT COMMENT '请求数据'")
    private String requestData;

    /**
     * 响应数据
     */
    @Column(name = "response_data", columnDefinition = "TEXT COMMENT '响应数据'")
    private String responseData;

    /**
     * 状态 0-成功 1-失败
     */
    @Column(name = "status", columnDefinition = "BIT DEFAULT '0' COMMENT '状态 0-成功 1-失败'")
    private Boolean status;

    /**
     * 错误信息
     */
    @Column(name = "error_msg", length = 500, columnDefinition = "VARCHAR(500) COMMENT '错误信息'")
    private String errorMsg;

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
     * transactionId
     */
    @Column(name = "transactionId", length = 100, columnDefinition = "VARCHAR(100) COMMENT 'transactionId'")
    private String transactionid;

    /**
     * 运营商
     */
    @Column(name = "operator", length = 255, columnDefinition = "VARCHAR(255) COMMENT '运营商'")
    private String operator;

    /**
     * 所属租户
     */
    @Column(name = "tenant", length = 255, columnDefinition = "VARCHAR(255) COMMENT '所属租户'")
    private String tenant;

    /**
     * 请求IP
     */
    @Column(name = "request_ip", length = 20, columnDefinition = "VARCHAR(20) COMMENT '请求IP'")
    private String requestIp;

    /**
     * 请求时间
     */
    @Column(name = "request_time", columnDefinition = "DATETIME COMMENT '请求时间'")
    private LocalDateTime requestTime;

    /**
     * 耗时(毫秒)
     */
    @Column(name = "cost_time", columnDefinition = "INT(11) COMMENT '耗时(毫秒)'")
    private Integer costTime;

}
