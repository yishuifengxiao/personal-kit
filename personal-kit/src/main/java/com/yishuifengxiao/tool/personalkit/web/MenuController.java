package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.BaseQuery;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenu;
import com.yishuifengxiao.tool.personalkit.domain.query.MenuQuery;
import com.yishuifengxiao.tool.personalkit.domain.request.MenuPermissionReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.MenuTree;
import com.yishuifengxiao.tool.personalkit.domain.vo.MenuVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.RoleMenuVo;
import com.yishuifengxiao.tool.personalkit.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/9 22:49
 * @since 1.0.0
 */
@Api(tags = {"菜单接口"})
@Valid
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @ApiOperation(value = "按照树形查询出所有的菜单")
    @PostMapping("/menuTree")
    public List<MenuTree> findMenuTree() {
        return menuService.findMenuTree();
    }

    @ApiOperation(value = "分页查询菜单")
    @PostMapping("/page")
    public Page<MenuVo> findPage(BaseQuery<SysMenu> pageQuery) {
        return menuService.findPage(pageQuery);
    }

    @ApiOperation(value = "更新菜单的权限")
    @PostMapping("/updateMenuPermission")
    public void updateMenuPermission(@Valid @RequestBody MenuPermissionReq req) {
        menuService.updateMenuPermission(req);
    }

    @ApiOperation(value = "查询出角色的菜单")
    @PostMapping("/findRoleMenu")
    public RoleMenuVo findRoleMenu(@RequestBody MenuQuery param) {
        return menuService.findRoleMenu(param.getRoleId(), param.getTopMenuId());
    }
}
