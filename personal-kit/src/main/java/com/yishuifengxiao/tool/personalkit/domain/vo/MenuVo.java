package com.yishuifengxiao.tool.personalkit.domain.vo;

import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenu;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/9 22:07
 * @since 1.0.0
 */
@Accessors(chain = true)
@Data
public class MenuVo extends SysMenu {

    private List<SysPermission> permissions;

    private String parentName;

    private String authName;

    private String typeName;

    private String roleNames;

    private List<MenuVo> children;
}
