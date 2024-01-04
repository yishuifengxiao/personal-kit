package com.yishuifengxiao.tool.personalkit.domain.query;

import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import jakarta.persistence.Column;
import lombok.Data;


/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/20 16:22
 * @since 1.0.0
 */
@Data
public class RoleQuery extends SysRole {

    @Column(name = "id")
    private String menuId;

    @Column(name = "name")
    private String menuName;
    
}
