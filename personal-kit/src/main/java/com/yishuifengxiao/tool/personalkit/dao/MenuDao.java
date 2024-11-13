package com.yishuifengxiao.tool.personalkit.dao;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2024/11/13 21:29
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class MenuDao {

    public List<SysMenu> findAllMenuByRole(String role) {
        String sql = """
                select sm.* from sys_menu sm ,sys_role_menu srm where sm.id = srm.menu_id and srm.role_id=? and is_show=1 
                """;
        return JdbcUtil.jdbcHelper().findAll(SysMenu.class, sql, role);
    }
}
