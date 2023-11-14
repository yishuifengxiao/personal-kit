package com.yishuifengxiao.tool.personalkit.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

import static com.yishuifengxiao.tool.personalkit.domain.constant.Constant.GENERIC_GENERATOR;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/7-15:27
 * @since 1.0.0
 */
@Table(name = "sys_role")
@Entity(name = "sys_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SysRole implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = GENERIC_GENERATOR)
    @Column(name = "id", length = 64, nullable = false, unique = true)
    private String id;


    @Column(length = 50, unique = true)
    private String name;


    private String description;

    @Column(name = "parent_id", length = 64, nullable = false)
    private String parentId;

    @Column(name = "is_embedded", length = 1, columnDefinition = "tinyint(1) default 0")
    private Integer embedded;
}
