package com.yishuifengxiao.tool.personalkit.domain.vo;

import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/9 22:20
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RoleMenuVo implements Serializable {

    private List<SysMenu> topMenus;

    private List<MenuTree> leftMenus;
}
