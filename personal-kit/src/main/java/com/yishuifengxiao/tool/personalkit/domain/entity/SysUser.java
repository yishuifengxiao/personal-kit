package com.yishuifengxiao.tool.personalkit.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.enums.UserStat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


import javax.annotation.Generated;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.yishuifengxiao.tool.personalkit.domain.constant.Constant.GENERIC_GENERATOR;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/6-16:04
 * @since 1.0.0
 */
// @formatter:off
@Table(name = "sys_user", indexes = {
        @Index(name = "idx_phone", columnList = "phone"),
        @Index(name = "idx_cert_no", columnList = "cert_no"),
        @Index(name = "idx_email", columnList = "email")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_username_ver", columnNames = {"username", "ver"})
})
@Entity(name = "sys_user")
@Where(clause = "ver = " + Constant.ACTIVE_DATA_VER )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
// @formatter:on
public class SysUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = GENERIC_GENERATOR)
    @Column(name = "id", length = 64, nullable = false, unique = true)
    private String id;

    @Column(nullable = false, length = 30)
    private String username;

    @Column(length = 50)
    private String nickname;

    @Column(length = 20)
    private String phone;

    @Column(length = 50)
    private String email;

    @Column(name = "cert_no", length = 18)
    private String certNo;

    @Column(nullable = false, length = 20)
    @JsonIgnore
    private String pwd;

    /**
     * 状态
     *
     * @see UserStat
     */
    @Column(length = 1, nullable = false, columnDefinition = "tinyint(1) default 0")
    private Integer stat;

    @Column(name = "create_time", nullable = false, updatable = false)
    @CreatedDate
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;


    @Column(length = 11, nullable = false, columnDefinition = "int(11) default 0")
    @JsonIgnore
    private Integer ver;

    @Column(name = "last_update_time", nullable = false)
    @LastModifiedDate
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdateTime;


    @Column(name = "is_embedded", length = 1, columnDefinition = "tinyint(1) default 0")
    @JsonIgnore
    private Integer embedded;
}
