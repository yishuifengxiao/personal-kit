package com.yishuifengxiao.tool.personalkit.service.sys;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysPermission;
import com.yishuifengxiao.tool.personalkit.domain.vo.PermissionVo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/9 20:07
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class PermissionService {
    public Page<PermissionVo> findPagePermission(PageQuery<SysPermission> pageQuery) {
        Page<SysPermission> page = JdbcUtil.jdbcHelper().findPage(pageQuery.query().orElse(new SysPermission()),
                true, pageQuery, Order.asc("module"), Order.asc("name"), Order.asc("url"));
        return page.map(v -> {
            PermissionVo permissionVo = BeanUtil.copy(v, new PermissionVo());
            String sql = "SELECT DISTINCT sm.* from sys_permission sp,sys_menu_permission smp,sys_menu sm where smp" +
                    ".menu_id=sm.id and smp.permission_id =sp.id and sp.id=?";
            return permissionVo;
        });
    }

    public List<SysPermission> findSysPermission(String menuId) {
        String sql = "SELECT p.* from sys_permission p,sys_menu_permission  r where p.id=r.permission_id and r.menu_id= :menuId";
        return JdbcUtil.jdbcHelper().find(SysPermission.class, sql, Map.of("menuId", menuId));
    }
}
