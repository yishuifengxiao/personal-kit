package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.entity.BaseQuery;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenu;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import com.yishuifengxiao.tool.personalkit.domain.vo.PermissionVo;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Collections;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/9 20:07
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class PermissionService {
    public Page<PermissionVo> findPagePermission(BaseQuery<SysPermission> pageQuery) {
        return JdbcUtil.jdbcHelper().findPage(pageQuery.query().orElse(new SysPermission()), pageQuery.size().intValue(), pageQuery.num().intValue()).map(v -> {
            PermissionVo permissionVo = BeanUtil.copy(v, new PermissionVo());
            String sql = "SELECT DISTINCT sm.* from sys_permission sp,sys_menu_permission smp,sys_menu sm where smp.menu_id=sm.id and smp.permission_id =sp.id and sp.id=?";
            permissionVo.setMenus(JdbcUtil.jdbcHelper().query(SysMenu.class, sql, v.getId()).orElse(Collections.EMPTY_LIST));
            return permissionVo;
        });
    }
}
