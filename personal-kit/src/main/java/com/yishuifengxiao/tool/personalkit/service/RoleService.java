package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.entity.BaseQuery;
import com.yishuifengxiao.common.tool.entity.BoolStat;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.entity.*;
import com.yishuifengxiao.tool.personalkit.domain.enums.RoleStat;
import com.yishuifengxiao.tool.personalkit.domain.query.RoleQuery;
import com.yishuifengxiao.tool.personalkit.domain.query.UserQuery;
import com.yishuifengxiao.tool.personalkit.domain.request.RoleUserReq;
import com.yishuifengxiao.tool.personalkit.domain.request.UserRoleReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.RoleVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.UserVo;
import com.yishuifengxiao.tool.personalkit.utils.QueryUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/17 15:52
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class RoleService {


    /**
     * 分页查询角色
     *
     * @param pageQuery
     * @return
     */
    public Page<RoleVo> findPageRole(BaseQuery<RoleQuery> pageQuery) {
        String sql = "SELECT DISTINCT sr.* from sys_role sr ,sys_role_menu srm ,sys_menu sm WHERE sr.id=srm.role_id and srm.menu_id=sm.id ";
        sql += QueryUtil.createAndSql(pageQuery.getQuery(), false, "sm");
        sql += QueryUtil.createAndSql(pageQuery.getQuery(), true, "sr");

        Page<SysRole> page = JdbcUtil.jdbcHelper().query(SysRole.class, pageQuery, sql);
        return page.map(v -> {
            RoleVo roleVo = BeanUtil.copy(v, new RoleVo());
            String psql = "SELECT DISTINCT sm.* from sys_role sr ,sys_role_menu srm ,sys_menu sm WHERE sr.id=srm.role_id and srm.menu_id=sm.id AND sr.id= ？";
            roleVo.setMenus(JdbcUtil.jdbcHelper().query(SysMenu.class, psql, v.getId()).orElse(Collections.EMPTY_LIST));
            return roleVo;
        });
    }


    public void addRole(RoleVo param) {
        //@formatter:off

        Assert.isNull("角色已存在", JdbcUtil.jdbcHelper().findOne(new SysRole().setName(param.getName().trim())));
        SysRole sysRole = BeanUtil.copy(param, new SysRole()).setId(IdWorker.snowflakeStringId()).setEmbedded(BoolStat.False.code()).setCreateTime(LocalDateTime.now()).setName(param.getName().trim());
        sysRole.setStat(null == param.getStat() ? RoleStat.ROLE_ENABLE.getCode() : RoleStat.code(param.getStat()).orElse(RoleStat.ROLE_DISABLE).getCode());
        if (StringUtils.isNotBlank(param.getParentId())) {
            Assert.lteZeroN("父角色不存在", JdbcUtil.jdbcHelper().countAll(new SysRole().setId(param.getParentId().trim())));
            sysRole.setParentId(param.getParentId().trim());
        } else {
            sysRole.setParentId(Constant.DEFAULT_ROOT_ID);
        }

        JdbcUtil.jdbcHelper().insertSelective(sysRole);

        this.updateRoleMenu(param.getId(),param.getMenus());
        //@formatter:on
    }

    public void updateRole(RoleVo param) {
        //@formatter:off
        SysRole role = JdbcUtil.jdbcHelper().findByPrimaryKey(SysRole.class, param.getId());
        Assert.isNotNull("记录不存在", role);
        if (StringUtils.isNotBlank(param.getName()) && !StringUtils.equalsIgnoreCase(param.getName().trim(), role.getName())) {

            Assert.isNull("角色已存在", JdbcUtil.jdbcHelper().findOne(new SysRole().setName(param.getName().trim())));
        }

        role.setName(StringUtils.trim(param.getName())).setDescription(param.getDescription()).setHomeUrl(param.getHomeUrl());

        if (null != param.getStat()) {
            role.setStat(RoleStat.code(param.getStat()).orElse(RoleStat.ROLE_DISABLE).getCode());
        }

        if (StringUtils.isNotBlank(param.getParentId()) && StringUtils.equalsIgnoreCase(param.getParentId().trim(), role.getParentId())) {
            Assert.lteZeroN("父角色不存在", JdbcUtil.jdbcHelper().countAll(new SysRole().setId(param.getParentId().trim())));
            role.setParentId(param.getParentId().trim());
        }

        JdbcUtil.jdbcHelper().updateByPrimaryKeySelective(role);
        this.updateRoleMenu(param.getId(),param.getMenus());

        //@formatter:on
    }

    public void updateRoleMenu(String roleId, List<SysMenu> menus) {
        //@formatter:off
        //删除旧的关联关系
        JdbcUtil.jdbcHelper().delete(new SysRoleMenu().setRoleId(roleId));

        Set<String> menuIds = DataUtil.stream(menus)
                .filter(Objects::nonNull)
                .filter(v -> StringUtils.isNotBlank(v.getId()))
                .filter(v -> JdbcUtil.jdbcHelper().countAll(new SysMenu().setId(v.getId().trim())) > 0)
                .distinct()
                .map(SysMenu::getId)
                .collect(Collectors.toSet());

        //增加隐藏菜单
        menuIds.addAll(JdbcUtil.jdbcHelper().findAll(new SysMenu().setIsShow(BoolStat.True.code())).stream().map(SysMenu::getId).filter(menuId->menuIds.stream().noneMatch(s->s.equals(menuId)))
                .distinct().collect(Collectors.toList()));


        menuIds.stream().filter(StringUtils::isNotBlank).map(v -> new SysRoleMenu(IdWorker.snowflakeStringId(),roleId, v)).forEach(JdbcUtil.jdbcHelper()::insertSelective);
        //@formatter:on
    }


    public void deleteRoles(List<String> ids) {
        for (String id : ids) {
            Assert.isNotBlank("记录主键不能为空", id);
            SysRole sysRole = JdbcUtil.jdbcHelper().findByPrimaryKey(SysRole.class, id.trim());
            Assert.isNotNull("记录不存在", sysRole);
            Assert.isFalse("内置角色不允许删除", sysRole.getEmbedded() == BoolStat.True.code());
            Assert.isFalse("未禁用的角色不允许删除", sysRole.getStat() == RoleStat.ROLE_ENABLE.getCode());
            JdbcUtil.jdbcHelper().deleteByPrimaryKey(SysRole.class, id.trim());
            // 删除关联关系
            JdbcUtil.jdbcHelper().delete(new SysRoleMenu().setRoleId(id.trim()));
            JdbcUtil.jdbcHelper().delete(new SysUserRole().setRoleId(id.trim()));
        }
    }


    public Page<UserVo> findPageUser(BaseQuery<UserQuery> pageQuery) {
        String sql = "SELECT  DISTINCT su.* from sys_user su,sys_user_role sur,sys_role sr where su.id=sur" + ".user_id  and sur.role_id=sr.id ";
        sql += QueryUtil.createAndSql(pageQuery.getQuery(), false, "sr");
        sql += QueryUtil.createAndSql(pageQuery.getQuery(), true, "su");
        Page<SysUser> page = JdbcUtil.jdbcHelper().query(SysUser.class, pageQuery, sql);
        return page.map(v -> {
            UserVo userVo = BeanUtil.copy(v, new UserVo());
            String psql = "SELECT  DISTINCT sr.* from sys_user_role sur,sys_role sr where sur.role_id=sr.id " + "and sur.user_id=?";
            Optional<List<SysRole>> roles = JdbcUtil.jdbcHelper().query(SysRole.class, psql, v.getId());
            userVo.setRoles(roles.orElse(Collections.EMPTY_LIST));
            return userVo;
        });
    }

    public void updateRoleUser(RoleUserReq roleUserReq) {
        //@formatter:off
        SysRole sysRole = JdbcUtil.jdbcHelper().findByPrimaryKey(SysRole.class, roleUserReq.getId().trim());
        Assert.isNotNull("角色不存在", sysRole);
        Assert.isFalse("角色已禁用", RoleStat.ROLE_DISABLE.getCode() == sysRole.getStat());
          DataUtil.stream(roleUserReq.getUserIds())
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .distinct()
                  // 排除超级管理员
                .filter(v -> !StringUtils.equalsIgnoreCase(v, Constant.DEFAULT_ROOT_ID))
                .filter(v->null!= JdbcUtil.jdbcHelper().findByPrimaryKey(SysUser.class,v))
                  //删除旧的关联关系
               .filter(v-> JdbcUtil.jdbcHelper().delete(new SysUserRole(v,roleUserReq.getId().trim()))>0)
                .map(v -> new SysUserRole(IdWorker.snowflakeStringId(), v.trim(), roleUserReq.getId().trim()))
                .forEach(JdbcUtil.jdbcHelper()::insertSelective);
        //@formatter:on
    }

    public void updateUserRole(UserRoleReq userRoleReq) {
        SysUser user = JdbcUtil.jdbcHelper().findByPrimaryKey(SysUser.class, userRoleReq.getId().trim());
        Assert.isNotNull("用户不存在", user);
        Assert.isFalse("内置用户禁止编辑", BoolStat.True.code() == user.getEmbedded());
        //删除旧的关联关系
        JdbcUtil.jdbcHelper().delete(new SysUserRole().setUserId(user.getId()));
        for (String roleId : DataUtil.stream(userRoleReq.getRoleIds()).distinct().filter(StringUtils::isNotBlank).map(String::trim).collect(Collectors.toList())) {
            SysRole sysRole = JdbcUtil.jdbcHelper().findByPrimaryKey(SysRole.class, roleId.trim());
            Assert.isNotNull(String.format("角色%s不存在", roleId), sysRole);
            Assert.isFalse(String.format("角色%s已禁用", sysRole.getName()), RoleStat.ROLE_DISABLE.getCode() == sysRole.getStat());

            JdbcUtil.jdbcHelper().insertSelective(new SysUserRole(IdWorker.snowflakeStringId(), user.getId(), sysRole.getId()));
        }

    }
}
