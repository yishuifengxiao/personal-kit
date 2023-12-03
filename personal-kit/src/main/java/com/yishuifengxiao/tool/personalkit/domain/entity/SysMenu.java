package com.yishuifengxiao.tool.personalkit.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yishuifengxiao.common.tool.validate.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.yishuifengxiao.tool.personalkit.domain.constant.Constant.GENERIC_GENERATOR;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/3 21:50
 * @since 1.0.0
 */
@Validated
@Table(name = "sys_menu", indexes = {@Index(name = "idx_parent_id", columnList = "parent_id")}, uniqueConstraints = {@UniqueConstraint(name = "uk_name", columnNames = {"name"})})
@Entity(name = "sys_menu")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SysMenu implements Serializable {

    @NotBlank(message = "待修改的记录主键不能为空", groups = {Group.Update.class})
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = GENERIC_GENERATOR)
    @Column(name = "id", length = 64, nullable = false, unique = true)
    private String id;


    @Size(max = 50, message = "角色名称最大50个字符", groups = {Group.Update.class, Group.Create.class})
    @Column(length = 50, unique = true)
    private String name;

    @Size(max = 255, message = "角色描述最大255个字符", groups = {Group.Update.class, Group.Create.class})
    private String description;

    @Column(name = "parent_id", length = 64, nullable = false)
    private String parentId;


    /**
     * 菜单位置，0:上部，1:侧边菜单
     *
     * @see com.yishuifengxiao.common.tool.entity.BoolStat
     */
    @Column(name = "type", length = 1, columnDefinition = "tinyint(1) default 0")
    private Integer type;

    /**
     * 是否需要鉴权，0:无需鉴权，1:需要鉴权
     *
     * @see com.yishuifengxiao.common.tool.entity.BoolStat
     */
    @Column(name = "auth", length = 1, columnDefinition = "tinyint(1) default 0")
    private Integer auth;

    @JsonIgnore
    @Column(name = "is_embedded", length = 1, columnDefinition = "tinyint(1) default 0")
    private Integer embedded;

    @Column(name = "create_time", nullable = false, updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

}
