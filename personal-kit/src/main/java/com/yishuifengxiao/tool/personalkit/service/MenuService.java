package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.entity.BoolStat;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenu;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenuPermission;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.request.MenuPermissionReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.MenuTree;
import com.yishuifengxiao.tool.personalkit.domain.vo.MenuVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.RoleMenuVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/8 23:25
 * @since 1.0.0
 */
@Service
public class MenuService {


    public List<MenuTree> findMenuTree() {
        // 顶部菜单
        List<SysMenu> menus = JdbcUtil.jdbcHelper().findAll(new SysMenu(), false);

        return menus.stream().filter(v -> BoolStat.isFalse(v.getType())).map(v -> {
                    MenuTree menuTree = BeanUtil.copy(v, new MenuTree());
                    List<MenuTree> children = buildTree(menus, v.getParentId());
                    menuTree.setChildrens(children);
                    return menuTree;
                }

        ).collect(Collectors.toList());
    }

    private List<MenuTree> buildTree(List<SysMenu> menuList, String parentId) {
        List<MenuTree> tree = new ArrayList<>();
        for (SysMenu menu : menuList) {
            if (menu.getParentId().equals(parentId)) {
                MenuTree menuTree = BeanUtil.copy(menu, new MenuTree());
                menuTree.setChildrens(buildTree(menuList, menu.getId()));
                tree.add(menuTree);
            }
        }
        return tree;
    }

    public Page<MenuVo> findPage(PageQuery<SysMenu> pageQuery) {
        return JdbcUtil.jdbcHelper().findPage(pageQuery.query().orElse(new SysMenu()), false,
                pageQuery.size().intValue(),
                pageQuery.num().intValue()).map(v -> {
            MenuVo vo = BeanUtil.copy(v, new MenuVo());
            String sql = "SELECT DISTINCT sp.* from sys_permission sp,sys_menu_permission smp where  smp" +
                    ".permission_id " + "=sp.id and smp.menu_id=?";
            List<SysPermission> list =
                    JdbcUtil.jdbcHelper().findAll(SysPermission.class, sql, v.getId());
            vo.setPermissions(list);
            return vo;
        });
    }


    public void updateMenuPermission(MenuPermissionReq req) {
        //@formatter:off
        SysMenu menu = JdbcUtil.jdbcHelper().findByPrimaryKey(SysMenu.class, req.getId().trim());
        Assert.isNotNull("记录不存在", menu);
        JdbcUtil.jdbcHelper().delete(new SysMenuPermission().setMenuId(menu.getId()));
        DataUtil.stream(req.getPermissionIds())
                .filter(StringUtils::isNotBlank)
                .filter(v -> JdbcUtil.jdbcHelper().countAll(new SysPermission().setId(v),false) > 0)
                .map(v -> new SysMenuPermission(menu.getId(), v))
                .forEach(JdbcUtil.jdbcHelper()::saveOrUpdate);
        //@formatter:on
    }


    public RoleMenuVo findRoleMenu(String roleId, String topMenuId) {
        SysRole sysRole = JdbcUtil.jdbcHelper().findByPrimaryKey(SysRole.class, roleId.trim());
        Assert.isNotNull("请选择一个正确的角色", sysRole);
        List<SysMenu> menus = null;
        if (BoolStat.isTrue(sysRole.getEmbedded())) {
            //内置且隐藏的角色
            menus = JdbcUtil.jdbcHelper().findAll(new SysMenu(),false);
        } else {
            String sql = "select sm.* from sys_menu sm ,sys_role_menu srm where sm.id = srm.menu_id and srm.role_id=?";
            menus = JdbcUtil.jdbcHelper().findAll(SysMenu.class, sql, sysRole.getId());
        }
        // 上部的菜单
        List<SysMenu> topMenus =
                menus.stream().filter(v -> BoolStat.isFalse(v.getType())).filter(v -> BoolStat.isTrue(v.getIsShow())).sorted(Comparator.comparing(SysMenu::getIdx)).collect(Collectors.toList());
        if (CollUtil.isEmpty(topMenus)) {
            return new RoleMenuVo(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        }
        //选中的上部菜单
        SysMenu selectTopMenu =
                topMenus.stream().filter(v -> StringUtils.equalsIgnoreCase(v.getRouterName(), topMenuId)).findFirst().orElse(DataUtil.first(topMenus));
        // 左侧的一级菜单
        List<SysMenu> leftFirsts =
                menus.stream().filter(v -> BoolStat.isTrue(v.getType())).filter(v -> StringUtils.equalsIgnoreCase(v.getParentId(), selectTopMenu.getId())).collect(Collectors.toList());

        List<SysMenu> allMenus = menus;
        List<MenuTree> menuTrees = leftFirsts.stream().map(v -> {
                    MenuTree menuTree = BeanUtil.copy(v, new MenuTree());
                    List<MenuTree> children = buildTree(allMenus, v.getId());
                    menuTree.setChildrens(children);
                    return menuTree;
                }

        ).collect(Collectors.toList());

        return new RoleMenuVo(topMenus, menuTrees);
    }


}
