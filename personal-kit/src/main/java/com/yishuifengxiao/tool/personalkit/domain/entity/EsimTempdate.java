package com.yishuifengxiao.tool.personalkit.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * EsimTempdate 实体类
 * 对应数据库表: esim_tempdate
 * 表注释: 模板管理表
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "esim_tempdate") // 模板管理表
public class EsimTempdate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID'", updatable = false)
    private Long id;

    /**
     * 模板名称
     */
    @Column(name = "temp_name", nullable = false, length = 100, columnDefinition = "VARCHAR(100) NOT NULL COMMENT '模板名称'")
    private String tempName;

    /**
     * Profile Type
     */
    @Column(name = "profile_type", nullable = false, length = 50, columnDefinition = "VARCHAR(50) NOT NULL COMMENT 'Profile Type'")
    private String profileType;

    /**
     * 所属运营商ID
     */
    @Column(name = "mon_id", nullable = false, length = 100, columnDefinition = "VARCHAR(100) NOT NULL COMMENT '所属运营商ID'")
    private String monId;

    /**
     * 模板内容
     */
    @Column(name = "temp_content", columnDefinition = "TEXT COMMENT '模板内容'")
    private String tempContent;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "DATETIME DEFAULT 'CURRENT_TIMESTAMP' COMMENT '创建时间'")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time", columnDefinition = "TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' COMMENT '更新时间'")
    private LocalDateTime updateTime;

}
