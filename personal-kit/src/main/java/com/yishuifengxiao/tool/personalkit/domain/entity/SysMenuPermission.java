package com.yishuifengxiao.tool.personalkit.domain.entity;

import com.yishuifengxiao.tool.personalkit.utils.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;


/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/7-13:24
 * @since 1.0.0
 */
@Table(name = "sys_menu_permission", uniqueConstraints = {
        @UniqueConstraint(name = "uk_menu_permission", columnNames = {"menu_id", "permission_id"})
})
@Entity(name = "sys_menu_permission")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SysMenuPermission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = CustomIdGenerator.GENERIC_GENERATOR)
    @Column(name = "id", length = 64, nullable = false, unique = true)
    private String id;


    @Column(name = "menu_id", nullable = false, length = 64)
    private String menuId;

    @Column(name = "permission_id", nullable = false, length = 64)
    private String permissionId;


    public SysMenuPermission(String menuId, String permissionId) {
        this.menuId = menuId;
        this.permissionId = permissionId;
    }
}
