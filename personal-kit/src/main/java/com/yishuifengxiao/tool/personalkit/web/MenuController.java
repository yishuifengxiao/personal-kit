package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenu;
import com.yishuifengxiao.tool.personalkit.domain.query.MenuQuery;
import com.yishuifengxiao.tool.personalkit.domain.request.MenuPermissionReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.MenuTree;
import com.yishuifengxiao.tool.personalkit.domain.vo.MenuVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.RoleMenuVo;
import com.yishuifengxiao.tool.personalkit.service.MenuService;
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
@Valid
@RestController
@RequestMapping("/sys/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/menuTree")
    public List<MenuTree> findMenuTree() {
        return menuService.findMenuTree();
    }

    @PostMapping("/page")
    public Page<MenuVo> findPage(@RequestBody PageQuery<SysMenu> pageQuery) {
        return menuService.findPage(pageQuery);
    }

    @PostMapping("/updateMenuPermission")
    public void updateMenuPermission(@Valid @RequestBody MenuPermissionReq req) {
        menuService.updateMenuPermission(req);
    }

    @PostMapping("/findRoleMenu")
    public RoleMenuVo findRoleMenu(@RequestBody MenuQuery param) {
        return menuService.findRoleMenu(param.getRoleId(), param.getTopMenuId());
    }
}
