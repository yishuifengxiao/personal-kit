package com.yishuifengxiao.tool.personalkit.web.system;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenu;
import com.yishuifengxiao.tool.personalkit.domain.query.MenuQuery;
import com.yishuifengxiao.tool.personalkit.domain.request.MenuPermissionReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.MenuTree;
import com.yishuifengxiao.tool.personalkit.domain.vo.MenuVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.RoleMenuVo;
import com.yishuifengxiao.tool.personalkit.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/9 22:49
 * @since 1.0.0
 */
@Tag(name = "菜单管理")
@Valid
@RestController
@RequestMapping("/sys/menu")
@RequiredArgsConstructor
@Trim
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "查询菜单树", description = "查询左侧的菜单树")
    @PostMapping("/menuTree")
    public List<MenuTree> findMenuTree() {
        return menuService.findMenuTree();
    }

    @Operation(summary = "分页查询", description = "分页查询菜单数据")
    @PostMapping("/page")
    public Page<MenuVo> findPage(@RequestBody PageQuery<SysMenu> pageQuery) {
        return menuService.findPage(pageQuery);
    }

    @Operation(summary = "更新菜单权限", description = "更新菜单拥有的权限资源")
    @PostMapping("/updateMenuPermission")
    public void updateMenuPermission(@Valid @RequestBody MenuPermissionReq req) {
        menuService.updateMenuPermission(req);
    }

    @Operation(summary = "查询个人菜单", description = "查询当前角色具有的菜单")
    @PostMapping("/findRoleMenu")
    public RoleMenuVo findRoleMenu(@RequestBody MenuQuery param) {
        return menuService.findRoleMenu(param.getRoleId(), param.getTopMenuId());
    }
}
