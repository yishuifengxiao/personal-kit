package com.yishuifengxiao.tool.personalkit.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadStat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.yishuifengxiao.tool.personalkit.domain.constant.Constant.GENERIC_GENERATOR;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/29 22:21
 * @since 1.0.0
 */

@Table(name = "disk_upload_record", indexes = {@Index(name = "idx_file_name", columnList = "file_name")})
@Entity(name = "disk_upload_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DiskUploadRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = GENERIC_GENERATOR)
    @Column(name = "id", length = 64, nullable = false, unique = true)
    private String id;

    @Column(name = "file_name", nullable = false)
    private String fileName;


    @Column(name = "user_id", length = 64)
    private String userId;

    /**
     * @see UploadStat
     */

    @Column(name = "stat", length = 1, columnDefinition = "tinyint(1) default 1")
    private Integer stat;

    private String msg;


    @Column(name = "create_time", nullable = false, updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    @Column(name = "finish_time", nullable = false, updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime finishTime;
}
