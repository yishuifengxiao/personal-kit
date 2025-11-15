package com.yishuifengxiao.tool.personalkit.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadStat;
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
 * @date 2023/11/29 22:21
 * @since 1.0.0
 */

@Table(name = "upload_record", indexes = {@Index(name = "idx_file_name", columnList = "file_name")})
@Entity(name = "upload_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UploadRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = CustomIdGenerator.GENERIC_GENERATOR)
    @Column(name = "id", length = 64, nullable = false, unique = true)
    private String id;

    /**
     * 文件名称
     */
    @Column(name = "file_name", nullable = false)
    private String fileName;


    /**
     * 文件夹名称
     */
    @Column(name = "folder_name")
    private String folderName;


    @Column(name = "user_id", length = 64)
    private String userId;

    /**
     * 关联的磁盘文件id
     */
    @Column(name = "disk_file_id", length = 64)
    private String diskFileId;


    /**
     * 0:上传中，1:上传失败，2:上传成功
     *
     * @see UploadStat
     */

    @Column(name = "stat", length = 1, columnDefinition = "tinyint(1) default 1")
    private Integer stat;


    /**
     * 上传模式 上传：0，解析：1
     *
     * @see UploadMode
     */
    @Column(name = "upload_mode", length = 1, columnDefinition = "tinyint(1) default 1")
    private Integer uploadMode;

    private String msg;


    @Column(name = "create_time", nullable = false, updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    @Column(name = "finish_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime finishTime;
}
