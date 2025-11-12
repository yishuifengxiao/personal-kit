package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.entity.BoolStat;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.dao.MenuDao;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenu;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenuPermission;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.request.MenuPermissionReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.MenuTree;
import com.yishuifengxiao.tool.personalkit.domain.vo.MenuVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.RoleMenuVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/8 23:25
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuDao menuDao;


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
        if (null == pageQuery.getQuery().getParentId()) {
            pageQuery.setQuery(pageQuery.getQuery().setParentId(Constant.DEFAULT_PARENT_ROOT_ID));
        }
        Page<SysMenu> page = JdbcUtil.jdbcHelper().findPage(pageQuery.query().orElse(new SysMenu()), false, pageQuery, Order.asc("idx"));

        // 获取当前页所有菜单的ID列表
        List<String> menuIds = page.getData().stream()
                .map(SysMenu::getId)
                .collect(Collectors.toList());

        // 如果没有数据，直接返回
        if (CollUtil.isEmpty(menuIds)) {
            return page.map(v -> {
                MenuVo vo = BeanUtil.copy(v, new MenuVo());
                buildMenuVoDetails(vo, v);
                return vo;
            });
        }

        // 查询这些菜单的所有子孙菜单
        String allChildrenSql = """
                WITH RECURSIVE menu_tree AS (
                    SELECT id, name, parent_id, router_name, router_path, icon, type, auth, idx, is_show, description
                    FROM sys_menu 
                    WHERE id IN (%s)
                    UNION ALL
                    SELECT sm.id, sm.name, sm.parent_id, sm.router_name, sm.router_path, sm.icon, sm.type, sm.auth, sm.idx, sm.is_show, sm.description
                    FROM sys_menu sm
                    INNER JOIN menu_tree mt ON sm.parent_id = mt.id
                )
                SELECT * FROM menu_tree
                """;

        String inClause = menuIds.stream().map(id -> "'" + id + "'").collect(Collectors.joining(","));
        String finalSql = String.format(allChildrenSql, inClause);

        List<SysMenu> allMenus = JdbcUtil.jdbcHelper().findAll(SysMenu.class, finalSql);

        // 构建ID到菜单的映射
        Map<String, SysMenu> menuMap = allMenus.stream()
                .collect(Collectors.toMap(SysMenu::getId, menu -> menu));

        // 构建ID到子菜单列表的映射
        Map<String, List<SysMenu>> childrenMap = new HashMap<>();
        for (SysMenu menu : allMenus) {
            childrenMap.computeIfAbsent(menu.getParentId(), k -> new ArrayList<>()).add(menu);
        }

        return page.map(v -> {
            MenuVo vo = BeanUtil.copy(v, new MenuVo());
            buildMenuVoDetails(vo, v);

            // 构建菜单树
            List<MenuVo> children = buildMenuTree(v.getId(), menuMap, childrenMap);
            vo.setChildren(children);

            return vo;
        });
    }

    private void buildMenuVoDetails(MenuVo vo, SysMenu menu) {
        vo.setParentName(Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(SysMenu.class, menu.getParentId()))
                .map(SysMenu::getName)
                .orElse(""));
        vo.setAuthName(BoolStat.isTrue(menu.getAuth()) ? "是" : "否");
        vo.setTypeName(BoolStat.isTrue(menu.getType()) ? "上部" : "左侧");

        String roleSql = """
                SELECT DISTINCT sr.`name` from sys_role sr,sys_role_menu srm where sr.id=srm.role_id and srm.menu_id=?
                """;
        String roleNames = JdbcUtil.jdbcHelper().findAll(String.class, roleSql, menu.getId()).stream()
                .collect(Collectors.joining(","));
        vo.setRoleNames(roleNames);

        String sql = "SELECT DISTINCT sp.* from sys_permission sp,sys_menu_permission smp " +
                "where smp.permission_id = sp.id and smp.menu_id=?";
        List<SysPermission> list = JdbcUtil.jdbcHelper().findAll(SysPermission.class, sql, menu.getId());
        vo.setPermissions(list);
    }

    private List<MenuVo> buildMenuTree(String parentId, Map<String, SysMenu> menuMap, Map<String, List<SysMenu>> childrenMap) {
        List<SysMenu> directChildren = childrenMap.getOrDefault(parentId, Collections.emptyList());

        return directChildren.stream()
                .sorted(Comparator.comparing(SysMenu::getIdx))
                .map(child -> {
                    MenuVo childVo = BeanUtil.copy(child, new MenuVo());
                    buildMenuVoDetails(childVo, child);

                    // 递归构建子菜单树
                    List<MenuVo> grandchildren = buildMenuTree(child.getId(), menuMap, childrenMap);
                    childVo.setChildren(grandchildren);

                    return childVo;
                })
                .collect(Collectors.toList());
    }



    public void updateMenuPermission(MenuPermissionReq req) {
        //@formatter:off
        SysMenu menu = JdbcUtil.jdbcHelper().findByPrimaryKey(SysMenu.class, req.getId().trim());
        Assert.isNotNull("记录不存在", menu);
        JdbcUtil.jdbcHelper().deleteByPrimaryKey(SysMenuPermission.class,
                JdbcUtil.jdbcHelper().findAll(new SysMenuPermission().setMenuId(menu.getId()),false).stream().map(SysMenuPermission::getId).toArray(Object[]::new) );
        CollUtil.stream(req.getPermissionIds())
                .filter(StringUtils::isNotBlank)
                .filter(v -> JdbcUtil.jdbcHelper().countAll(new SysPermission().setId(v),false) > 0)
                .map(v -> new SysMenuPermission(menu.getId(), v))
                .forEach(JdbcUtil.jdbcHelper()::saveOrUpdate);
        //@formatter:on
    }


    public RoleMenuVo findRoleMenu(String roleId, String topMenuId) {
        roleId = StringUtils.isBlank(roleId) ? Constant.DEFAULT_ROOT_ID : roleId.trim();
        SysRole sysRole = JdbcUtil.jdbcHelper().findByPrimaryKey(SysRole.class, roleId.trim());
        Assert.isNotNull("请选择一个正确的角色", sysRole);
        List<SysMenu> list = menuDao.findAllMenuByRole(roleId);
        if (CollUtil.isEmpty(list)) {
            return new RoleMenuVo(Collections.emptyList(), Collections.emptyList());
        }
        // 上部的菜单
        List<SysMenu> topMenus = list.stream().filter(v -> BoolStat.isFalse(v.getType())).sorted(Comparator.comparing(SysMenu::getIdx)).collect(Collectors.toList());
        if (CollUtil.isEmpty(topMenus)) {
            return new RoleMenuVo(Collections.emptyList(), Collections.emptyList());
        }
        SysMenu selectTop = topMenus.stream().filter(v -> StringUtils.equalsIgnoreCase(v.getId(), topMenuId)).findFirst().orElse(topMenus.get(0));


        List<MenuTree> menuTrees = buildTree(list, selectTop);


        return new RoleMenuVo(topMenus, menuTrees);
    }

    public static List<MenuTree> buildTree(List<SysMenu> nodes, SysMenu selectTop) {
        List<MenuTree> trees = nodes.stream().filter(v -> StringUtils.equalsIgnoreCase(v.getParentId(), selectTop.getId())).sorted(Comparator.comparing(SysMenu::getIdx)).map(v -> {
            MenuTree firstLevel = BeanUtil.copy(v, new MenuTree());

            List<MenuTree> secondLevel = nodes.stream().filter(s -> StringUtils.equalsIgnoreCase(s.getParentId(), firstLevel.getId())).sorted(Comparator.comparing(SysMenu::getIdx)).map(s -> BeanUtil.copy(s, new MenuTree())).collect(Collectors.toList());
            firstLevel.setChildrens(secondLevel);
            return firstLevel;
        }).collect(Collectors.toList());


        return trees;
    }

}
