package com.yishuifengxiao.tool.personalkit.domain.entity.esim;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * EsimMon 实体类
 * 对应数据库表: esim_mon
 * 表注释: 运营商管理表
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "esim_mon") // 运营商管理表
public class EsimMon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID'", updatable = false)
    private Long id;

    /**
     * 运营商OID，格式如：v1.v2.v3...vn
     */
    @Column(name = "mon_oid", nullable = false, length = 255, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '运营商OID，格式如：v1.v2.v3...vn'")
    private String monOid;

    /**
     * 运营商名称
     */
    @Column(name = "mon_name", nullable = false, length = 255, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '运营商名称'")
    private String monName;

    /**
     * 简称
     */
    @Column(name = "mon_short_name", nullable = false, length = 100, columnDefinition = "VARCHAR(100) NOT NULL COMMENT '简称'")
    private String monShortName;

    /**
     * 状态 1:启用 0:禁用
     */
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT(3) NOT NULL DEFAULT '1' COMMENT '状态 1:启用 0:禁用'")
    private Integer status;

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
     * 创建人
     */
    @Column(name = "create_by", length = 64, columnDefinition = "VARCHAR(64) COMMENT '创建人'")
    private String createBy;

    /**
     * 更新人
     */
    @Column(name = "update_by", length = 64, columnDefinition = "VARCHAR(64) COMMENT '更新人'")
    private String updateBy;

}
