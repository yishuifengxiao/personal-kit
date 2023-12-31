package com.yishuifengxiao.tool.personalkit.domain.vo;

import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenu;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/23 19:44
 * @since 1.0.0
 */
@Data
public class PermissionVo extends SysPermission implements Serializable {

    private List<SysMenu> menus;
}
