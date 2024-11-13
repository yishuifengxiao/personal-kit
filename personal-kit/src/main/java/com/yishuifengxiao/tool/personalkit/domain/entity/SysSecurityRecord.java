package com.yishuifengxiao.tool.personalkit.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yishuifengxiao.common.security.support.Strategy;
import com.yishuifengxiao.tool.personalkit.utils.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/17 11:25
 * @since 1.0.0
 */
@Table(name = "sys_security_record", indexes = {@Index(name = "idx_name", columnList = "username")})
@Entity(name = "sys_security_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SysSecurityRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = CustomIdGenerator.GENERIC_GENERATOR)
    @Column(name = "id", length = 64, nullable = false, unique = true)
    private String id;

    private String username;


    @Column(name = "content_type")
    private String contentType;

    @Column(name = "user_agent")
    private String userAgent;

    private String referer;

    @Column(length = 50)
    private String ip;

    /**
     * @see Strategy
     */
    private Integer strategy;

    private String msg;

    @Column(name = "create_time", nullable = false, updatable = false)
    @CreatedDate
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;
}
