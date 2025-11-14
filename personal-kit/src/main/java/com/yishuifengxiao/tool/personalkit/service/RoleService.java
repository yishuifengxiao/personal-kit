package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.common.tool.utils.ValidateUtils;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenu;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRoleMenu;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUserRole;
import com.yishuifengxiao.tool.personalkit.domain.enums.RoleStat;
import com.yishuifengxiao.tool.personalkit.domain.query.RoleQuery;
import com.yishuifengxiao.tool.personalkit.domain.vo.RoleVo;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
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
public class RoleService {


    /**
     * 分页查询角色
     *
     * @param pageQuery
     * @return
     */
    public Page<SysRole> findPageRole(PageQuery<RoleQuery> pageQuery) {
        RoleQuery roleQuery = pageQuery.query().orElse(new RoleQuery());


        Page<SysRole> result = JdbcUtil.jdbcHelper().findPage(roleQuery, true, pageQuery);


        return result;
    }


    public void addRole(RoleVo param) {
        //@formatter:off

        Assert.isNull("角色已存在", JdbcUtil.jdbcHelper().findOne(new SysRole().setName(param.getName().trim()),false));

        SysRole sysRole = BeanUtil.copy(param, new SysRole()).setId(IdWorker.snowflakeStringId()).setCreateTime(LocalDateTime.now()).setName(param.getName().trim());

        sysRole.setStat(null == param.getStat() ? RoleStat.ROLE_ENABLE.code() : RoleStat.code(param.getStat()).orElse(RoleStat.ROLE_DISABLE).code());

        if (StringUtils.isNotBlank(param.getParentId())) {
            Assert.lteZeroN("父角色不存在", JdbcUtil.jdbcHelper().countAll(new SysRole().setId(param.getParentId().trim()),
                    false));
            sysRole.setParentId(param.getParentId().trim());
        } else {
            sysRole.setParentId(Constant.DEFAULT_ROOT_ID);
        }

        JdbcUtil.jdbcHelper().saveOrUpdate(sysRole);

        //@formatter:on
    }

    public void updateRole(RoleVo param) {
        //@formatter:off
        SysRole role =
                Optional.ofNullable( JdbcUtil.jdbcHelper().findByPrimaryKey(SysRole.class, param.getId())).filter(r->!RoleStat.ROLE_INIT.equalCode(r.getStat())).orElseThrow(ValidateUtils.orElseThrow("记录不存在"));

        if (StringUtils.isNotBlank(param.getName()) && !StringUtils.equalsIgnoreCase(param.getName().trim(), role.getName())) {

            Assert.isNull("角色已存在", JdbcUtil.jdbcHelper().findOne(new SysRole().setName(param.getName().trim()),false));
        }

        role.setName(StringUtils.trim(param.getName())).setDescription(param.getDescription()).setHomeUrl(param.getHomeUrl());

        if (null != param.getStat()) {
            role.setStat(RoleStat.code(param.getStat()).orElse(RoleStat.ROLE_DISABLE).code());
        }

        if (StringUtils.isNotBlank(param.getParentId()) && StringUtils.equalsIgnoreCase(param.getParentId().trim(), role.getParentId())) {
            Assert.lteZeroN("父角色不存在", JdbcUtil.jdbcHelper().countAll(new SysRole().setId(param.getParentId().trim()),
                    false));
            role.setParentId(param.getParentId().trim());
        }

        JdbcUtil.jdbcHelper().updateByPrimaryKeySelective(role);

        //@formatter:on
    }

    public void updateRoleMenu(String roleId, List<SysMenu> menus) {
        Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(SysRole.class, roleId)).filter(r -> !RoleStat.ROLE_INIT.equalCode(r.getStat())).orElseThrow(ValidateUtils.orElseThrow("记录不存在"));
        //@formatter:off
        //删除旧的关联关系
        JdbcUtil.jdbcHelper().jdbcTemplate().update("DELETE FROM sys_role_menu WHERE role_id = ?", roleId);
        List<SysRoleMenu> menuList = menus.stream().map(menu -> new SysRoleMenu(roleId+menu.getId(),roleId,menu.getId())).collect(Collectors.toList());
        JdbcUtil.jdbcHelper().saveAll(menuList);
        //@formatter:on
    }


    public void deleteRoles(List<String> ids) {
        for (String id : ids) {
            Assert.isNotBlank("记录主键不能为空", id);
            SysRole sysRole =
                    Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(SysRole.class, id)).filter(r -> !RoleStat.ROLE_INIT.equalCode(r.getStat())).orElseThrow(ValidateUtils.orElseThrow("记录不存在"));
            Assert.isTrue("未禁用的角色不允许删除", RoleStat.ROLE_DISABLE.equalCode(sysRole.getStat()));
            JdbcUtil.jdbcHelper().deleteByPrimaryKey(SysRole.class, id.trim());

            // 删除关联关系
            JdbcUtil.jdbcHelper().deleteByPrimaryKey(SysRoleMenu.class,
                    JdbcUtil.jdbcHelper().findAll(new SysRoleMenu().setRoleId(id.trim()), false).stream().map(SysRoleMenu::getId).toArray(Object[]::new));
            JdbcUtil.jdbcHelper().deleteByPrimaryKey(SysUserRole.class,
                    JdbcUtil.jdbcHelper().findAll(new SysUserRole().setRoleId(id.trim()), false).stream().map(SysUserRole::getId).toArray(Object[]::new));
        }
    }


    public List<String> findMenu(@NotBlank(message = "请选择一个记录") String id) {
        return JdbcUtil.jdbcHelper().findAll(new SysRoleMenu().setRoleId(id.trim()), false).stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }
}
