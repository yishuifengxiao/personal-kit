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
import com.yishuifengxiao.tool.personalkit.domain.vo.PermissionVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.RoleVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.UserVo;
import com.yishuifengxiao.tool.personalkit.utils.QueryUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/17 15:52
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class SysService {

    public Page<PermissionVo> findPagePermission(BaseQuery<SysPermission> pageQuery) {
        return JdbcUtil.jdbcHelper().findPage(pageQuery.query().orElse(new SysPermission()), pageQuery.size().intValue(), pageQuery.num().intValue()).map(v -> {
            PermissionVo permissionVo = BeanUtil.copy(v, new PermissionVo());
            String sql = "SELECT DISTINCT sr.* from sys_role sr,sys_relation_role_permission srp where sr.id=srp" + ".role_id AND srp.permission_id = ?";
            permissionVo.setRoles(JdbcUtil.jdbcHelper().query(SysRole.class, sql, v.getId()).orElse(Collections.EMPTY_LIST));
            return permissionVo;
        });
    }

    /**
     * 分页查询角色
     *
     * @param pageQuery
     * @return
     */
    public Page<RoleVo> findPageRole(BaseQuery<RoleQuery> pageQuery) {
        String sql = "SELECT DISTINCT sr.* from sys_role sr,sys_relation_role_permission srp,sys_permission sp where " + "sr.id=srp" + ".role_id and srp.permission_id=sp.id ";
        sql += QueryUtil.createAndSql(pageQuery.getQuery(), false, "sp");
        sql += QueryUtil.createAndSql(pageQuery.getQuery(), true, "sr");

        Page<SysRole> page = JdbcUtil.jdbcHelper().query(SysRole.class, pageQuery, sql);
        return page.map(v -> {
            RoleVo roleVo = BeanUtil.copy(v, new RoleVo());
            String psql = "SELECT DISTINCT sp.* from sys_permission sp,sys_relation_role_permission srp where sp.id=srp" + ".permission_id and srp.role_id=" + v.getId();
            roleVo.setPermissions(JdbcUtil.jdbcHelper().query(SysPermission.class, psql).orElse(Collections.EMPTY_LIST));
            return roleVo;
        });
    }


    public void addRole(RoleVo param) {

        Assert.isNull("角色已存在", JdbcUtil.jdbcHelper().findOne(new SysRole().setName(param.getName().trim())));
        SysRole sysRole = BeanUtil.copy(param, new SysRole()).setId(IdWorker.snowflakeStringId()).setEmbedded(BoolStat.False.code()).setCreateTime(LocalDateTime.now()).setName(param.getName().trim());
        if (null == param.getStat()) {
            sysRole.setStat(RoleStat.ROLE_ENABLE.getCode());
        } else {
            sysRole.setStat(RoleStat.code(param.getStat()).orElse(RoleStat.ROLE_DISABLE).getCode());
        }
        if (StringUtils.isNotBlank(param.getParentId())) {
            Assert.lteZeroN("父角色不存在", JdbcUtil.jdbcHelper().countAll(new SysRole().setId(param.getParentId().trim())));
            sysRole.setParentId(param.getParentId().trim());
        } else {
            sysRole.setParentId(Constant.DEFAULT_ROOT_ID);
        }

        JdbcUtil.jdbcHelper().insertSelective(sysRole);

        List<SysRelationRolePermission> rolePermissions = DataUtil.stream(param.getPermissions()).filter(Objects::nonNull).filter(v -> StringUtils.isNotBlank(v.getId()) && JdbcUtil.jdbcHelper().countAll(new SysPermission().setId(v.getId().trim())) > 0).distinct().map(v -> new SysRelationRolePermission(IdWorker.snowflakeStringId(), sysRole.getId(), v.getId())).collect(Collectors.toList());
        rolePermissions.stream().forEach(JdbcUtil.jdbcHelper()::insertSelective);
    }

    public void updateRole(RoleVo param) {
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
        JdbcUtil.jdbcHelper().delete(new SysRelationRolePermission().setRoleId(role.getId()));

        List<SysRelationRolePermission> rolePermissions = DataUtil.stream(param.getPermissions()).filter(Objects::nonNull).filter(v -> StringUtils.isNotBlank(v.getId()) && JdbcUtil.jdbcHelper().countAll(new SysPermission().setId(v.getId().trim())) > 0).distinct().map(v -> new SysRelationRolePermission(IdWorker.snowflakeStringId(), role.getId(), v.getId())).collect(Collectors.toList());
        rolePermissions.stream().forEach(JdbcUtil.jdbcHelper()::insertSelective);
    }


    public void deleteRoles(List<String> ids) {
        for (String id : ids) {
            Assert.isNotBlank("记录主键不能为空", id);
            SysRole sysRole = JdbcUtil.jdbcHelper().findByPrimaryKey(SysRole.class, id.trim());
            Assert.isNotNull("记录不存在", sysRole);
            Assert.isFalse("内置角色不允许删除", sysRole.getEmbedded() == BoolStat.True.code());
            Assert.isFalse("未禁用的角色不允许删除", sysRole.getStat() == RoleStat.ROLE_ENABLE.getCode());
            JdbcUtil.jdbcHelper().deleteByPrimaryKey(SysRole.class, id.trim());
        }
    }


    public Page<UserVo> findPageUser(BaseQuery<UserQuery> pageQuery) {
        String sql = "SELECT  DISTINCT su.* from sys_user su,sys_relation_user_role sur,sys_role sr where su.id=sur" + ".user_id  and sur.role_id=sr.id ";
        sql += QueryUtil.createAndSql(pageQuery.getQuery(), false, "sr");
        sql += QueryUtil.createAndSql(pageQuery.getQuery(), true, "su");
        Page<SysUser> page = JdbcUtil.jdbcHelper().query(SysUser.class, pageQuery, sql);
        return page.map(v -> {
            UserVo userVo = BeanUtil.copy(v, new UserVo());
            String psql = "SELECT  DISTINCT sr.* from sys_relation_user_role sur,sys_role sr where sur.role_id=sr.id " + "and sur.user_id=?";
            Optional<List<SysRole>> roles = JdbcUtil.jdbcHelper().query(SysRole.class, psql, v.getId());
            userVo.setRoles(roles.orElse(Collections.EMPTY_LIST));
            return userVo;
        });
    }

    public void updateRoleUser(RoleUserReq roleUserReq) {
        SysRole sysRole = JdbcUtil.jdbcHelper().findByPrimaryKey(SysRole.class, roleUserReq.getId().trim());
        Assert.isNotNull("角色不存在", sysRole);
        Assert.isFalse("角色已禁用", RoleStat.ROLE_DISABLE.getCode() == sysRole.getStat());
        DataUtil.stream(roleUserReq.getUserIds()).filter(StringUtils::isNotBlank).distinct().filter(v -> !StringUtils.equalsIgnoreCase(v, Constant.DEFAULT_ROOT_ID)).filter(v -> null == JdbcUtil.jdbcHelper().findOne(new SysRelationUserRole(v.trim(), roleUserReq.getId().trim()))).map(v -> new SysRelationUserRole(IdWorker.snowflakeStringId(), v.trim(), roleUserReq.getId().trim())).forEach(JdbcUtil.jdbcHelper()::insertSelective);
    }

    public void updateUserRole(UserRoleReq userRoleReq) {
        SysUser user = JdbcUtil.jdbcHelper().findByPrimaryKey(SysUser.class, userRoleReq.getId().trim());
        Assert.isNotNull("用户不存在", user);
        Assert.isFalse("内置用户禁止编辑", BoolStat.True.code() == user.getEmbedded());
        JdbcUtil.jdbcHelper().delete(new SysRelationUserRole().setUserId(user.getId()));
        DataUtil.stream(userRoleReq.getRoleIds()).distinct().filter(StringUtils::isNotBlank).map(String::trim).map(v -> {
            SysRole sysRole = JdbcUtil.jdbcHelper().findByPrimaryKey(SysRole.class, v.trim());
            Assert.isNotNull(String.format("角色%s不存在", v), sysRole);
            Assert.isFalse(String.format("角色%s已禁用", sysRole.getName()), RoleStat.ROLE_DISABLE.getCode() == sysRole.getStat());

            return new SysRelationUserRole(IdWorker.snowflakeStringId(), user.getId(), sysRole.getId());
        }).forEach(JdbcUtil.jdbcHelper()::insertSelective);
    }
}
