package com.yishuifengxiao.tool.personalkit.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yishuifengxiao.tool.personalkit.utils.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author qingteng
 * @version 1.0.0
 * @date 2024/10/31 21:18
 * @since 1.0.0
 */
@Table(name = "http_log", indexes = {@Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_uri", columnList = "uri")})
@Entity(name = "http_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class HttpLog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = CustomIdGenerator.GENERIC_GENERATOR)
    @Column(name = "id", length = 64, nullable = false, unique = true)
    private String id;

    private String uri;

    @Column(name = "method", length = 10, columnDefinition = "varchar(32)")
    private String method;

    @Column(name = "user_id", length = 64, columnDefinition = "varchar(64)")
    private String userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "header_param", columnDefinition = "longtext")
    private String headerParam;

    @Column(name = "query_param", columnDefinition = "longtext")
    private String queryParam;

    @Column(name = "request_body", columnDefinition = "longtext")
    private String requestBody;

    @Column(name = "response_body", columnDefinition = "longtext")
    private String responseBody;

    @Column(name = "use_millis", columnDefinition = "bigint")
    private Long useMillis;

    private String mark;

    @Column(name = "create_time", nullable = false, updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;
}
