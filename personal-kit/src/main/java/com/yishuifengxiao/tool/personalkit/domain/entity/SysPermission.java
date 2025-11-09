package com.yishuifengxiao.tool.personalkit.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yishuifengxiao.tool.personalkit.utils.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;


/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/7-15:34
 * @since 1.0.0
 */
@Table(name = "sys_permission", indexes = {@Index(name = "idx_name", columnList = "name"), @Index(name = "idx_url", columnList = "url")})
@Entity(name = "sys_permission")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SysPermission implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = CustomIdGenerator.GENERIC_GENERATOR)
    @Column(name = "id", length = 64, nullable = false, unique = true)
    private String id;

    private String module;

    private String name;

    private String description;

    private String url;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "context_path")
    private String contextPath;

    @Column(name = "application_name")
    private String applicationName;


    @JsonIgnore
    @Column(name = "is_embedded", length = 1, columnDefinition = "tinyint(1) default 0")
    private Integer embedded;
}
