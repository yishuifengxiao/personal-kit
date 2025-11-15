package com.yishuifengxiao.tool.personalkit.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
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
 * @date 2023/11/28 22:47
 * @since 1.0.0
 */
@Table(name = "disk_file", indexes = {@Index(name = "idx_file_name", columnList = "file_name"), @Index(name =
        "idx_original_file_name", columnList = "original_file_name"),
        @Index(name = "idx_upload_id", columnList = "upload_id")})
@Entity(name = "disk_file")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DiskFile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = CustomIdGenerator.GENERIC_GENERATOR)
    @Column(name = "id", length = 64, nullable = false, unique = true)
    private String id;


    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "folder_id", length = 64)
    private String folderId;

    @Column(name = "user_id", length = 64)
    private String userId;


    @Column(name = "oss_id", length = 64)
    private String ossId;


    @Column(name = "file_md5", length = 32)
    private String fileMd5;


    /**
     * 文件后缀
     */
    @Column(name = "suffix", length = 50)
    private String suffix;


    @Column(name = "file_url", length = 100)
    private String fileUrl;

    /**
     * 本地路径
     */
    @Column(name = "local_path")
    private String localPath;

    @Column(name = "original_file_name")
    private String originalFileName;


    @Column(name = "upload_record_id", length = 64)
    private String uploadRecordId;


    private Long size;


    /**
     * 模式：0:仅上传，1:上传且解析
     *
     * @see UploadMode
     */
    @Column(length = 1, nullable = false, columnDefinition = "tinyint(1) default 0")
    private Integer mode;

    @Column(name = "create_time", nullable = false, updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

}
