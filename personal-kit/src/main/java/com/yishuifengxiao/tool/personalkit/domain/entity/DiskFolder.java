package com.yishuifengxiao.tool.personalkit.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
 * @date 2023/11/28 22:47
 * @since 1.0.0
 */
@Table(name = "disk_folder", indexes = {@Index(name = "idx_user_id", columnList = "user_id"), @Index(name = "idx_parent_id", columnList = "parent_id")}, uniqueConstraints = {@UniqueConstraint(name = "uk_folder_name", columnNames = {"folder_name", "parent_id", "user_id"})})
@Entity(name = "disk_folder")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DiskFolder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = GENERIC_GENERATOR)
    @Column(name = "id", length = 64, nullable = false, unique = true)
    private String id;


    @Column(name = "folder_name", nullable = false)
    private String folderName;

    @Column(name = "parent_id", length = 64)
    private String parentId;

    @Column(name = "user_id", length = 64)
    private String userId;


    @Column(name = "create_time", nullable = false, updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

}
