package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.entity.BoolStat;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenu;
import com.yishuifengxiao.tool.personalkit.domain.vo.MenuTree;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<MenuTree> findAllMenus() {
        // 顶部菜单
        List<SysMenu> menus = JdbcUtil.jdbcHelper().findAll(new SysMenu());
        return menus.stream().filter(v -> BoolStat.isFalse(v.getType())).map(v ->
                {
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



}
