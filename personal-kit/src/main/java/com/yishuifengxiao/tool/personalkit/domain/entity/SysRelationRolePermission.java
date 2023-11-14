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
 * @date 2023/11/7-13:24
 * @since 1.0.0
 */
@Table(name = "sys_relation_role_permission", uniqueConstraints = {
        @UniqueConstraint(name = "uk_role_permission", columnNames = {"role_id", "permission_id"})
})
@Entity(name = "sys_relation_role_permission")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SysRelationRolePermission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = GENERIC_GENERATOR)
    @Column(name = "id", length = 64, nullable = false, unique = true)
    private String id;


    @Column(name = "role_id", nullable = false, length = 64)
    private String roleId;

    @Column(name = "permission_id", nullable = false, length = 64)
    private String permissionId;


    public SysRelationRolePermission(String roleId, String permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }
}
