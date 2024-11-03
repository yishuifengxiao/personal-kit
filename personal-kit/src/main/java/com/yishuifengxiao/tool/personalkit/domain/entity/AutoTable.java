package com.yishuifengxiao.tool.personalkit.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2024/11/3 22:30
 * @since 1.0.0
 */
@Data
@Table(name = "auto_table")
@AllArgsConstructor
@NoArgsConstructor
public class AutoTable implements Serializable {

    @Id
    @Column(name = "id", columnDefinition = " bigint NOT NULL AUTO_INCREMENT ")
    private Long id;

    @Column(name = "name", columnDefinition = " varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL " +
            "DEFAULT NULL ")
    private String name;

    @Column(name = "age", columnDefinition = " int NULL DEFAULT NULL ")
    private Integer age;
}
