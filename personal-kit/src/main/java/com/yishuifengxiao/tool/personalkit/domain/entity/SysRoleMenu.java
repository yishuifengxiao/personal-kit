package com.yishuifengxiao.tool.personalkit.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;

import static com.yishuifengxiao.tool.personalkit.domain.constant.Constant.GENERIC_GENERATOR;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/9 18:08
 * @since 1.0.0
 */
@Table(name = "sys_role_menu", uniqueConstraints = {
        @UniqueConstraint(name = "uk_role_menu", columnNames = {"role_id", "menu_id"})
})
@Entity(name = "sys_role_menu")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SysRoleMenu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = GENERIC_GENERATOR)
    @Column(name = "id", length = 64, nullable = false, unique = true)
    private String id;


    @Column(name = "role_id", nullable = false, length = 64)
    private String roleId;

    @Column(name = "menu_id", nullable = false, length = 64)
    private String menuId;


    public SysRoleMenu(String roleId, String menuId) {
        this.roleId = roleId;
        this.menuId = menuId;
    }
}
