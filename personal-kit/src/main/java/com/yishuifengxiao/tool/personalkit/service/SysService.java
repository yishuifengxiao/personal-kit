package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.entity.BaseQuery;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.query.RoleQuery;
import com.yishuifengxiao.tool.personalkit.domain.vo.PermissionVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.RoleVo;
import com.yishuifengxiao.tool.personalkit.utils.QueryUtil;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/17 15:52
 * @since 1.0.0
 */
@Component
public class SysService {

    public Page<PermissionVo> findPagePermission(BaseQuery<SysPermission> pageQuery) {
        return JdbcUtil.jdbcHelper().findPage(pageQuery.query().orElse(new SysPermission()),
                pageQuery.size().intValue(), pageQuery.num().intValue()).map(v -> {
            PermissionVo permissionVo = BeanUtil.copy(v, new PermissionVo());
            String sql = "SELECT DISTINCT sr.* from sys_role sr,sys_relation_role_permission srp where sr.id=srp" +
                    ".role_id AND srp.permission_id = ?";
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
        String sql = "SELECT DISTINCT sr.* from sys_role sr,sys_relation_role_permission srp,sys_permission sp where " +
                "sr.id=srp" + ".role_id and srp.permission_id=sp.id ";
        sql += QueryUtil.createAndSql(pageQuery.getQuery(), false, "sp");
        sql += QueryUtil.createAndSql(pageQuery.getQuery(), true, "sr");

        Page<SysRole> page = JdbcUtil.jdbcHelper().query(SysRole.class, pageQuery, sql);
        return page.map(v -> {
            RoleVo roleVo = BeanUtil.copy(v, new RoleVo());
            String psql = "SELECT DISTINCT sp.* from sys_permission sp,sys_relation_role_permission srp where sp.id=srp" +
                    ".permission_id and srp.role_id=" + v.getId();
            roleVo.setPermissions(JdbcUtil.jdbcHelper().query(SysPermission.class, psql).orElse(Collections.EMPTY_LIST));
            return roleVo;
        });
    }
}
