package com.yishuifengxiao.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * UserToken 实体类
 * 对应数据库表: user_token
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_token")
public class UserToken implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键'", updatable = false)
    private Long id;

    /**
     * token信息
     */
    @Column(name = "token", length = 255, columnDefinition = "VARCHAR(255)  COMMENT 'token信息'")
    private String token;

    /**
     * 用户
     */
    @Column(name = "user_name", length = 255, columnDefinition = "VARCHAR(255)  COMMENT '用户'")
    private String userName;

    /**
     * 设备id
     */
    @Column(name = "device_id", length = 255, columnDefinition = "VARCHAR(255)  COMMENT '设备id'")
    private String deviceId;

    /**
     * token的详细信息
     */
    @Column(name = "value", columnDefinition = "TEXT  COMMENT 'token的详细信息'")
    private String value;

    /**
     * token颁发时间
     */
    @Column(name = "issue_at", columnDefinition = "DATETIME NULL COMMENT 'token颁发时间'")
    private LocalDateTime issueAt;

    /**
     * token过期时间
     */
    @Column(name = "expire_at", columnDefinition = "DATETIME NULL COMMENT 'token过期时间'")
    private LocalDateTime expireAt;

}
