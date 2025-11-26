package com.yishuifengxiao.tool.personalkit.domain.query;

import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysRole;
import lombok.Data;


/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/20 16:22
 * @since 1.0.0
 */
@Data
public class RoleQuery extends SysRole {

    private String menuId;

    private String menuName;

}
