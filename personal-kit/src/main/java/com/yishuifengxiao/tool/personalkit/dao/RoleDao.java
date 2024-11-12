package com.yishuifengxiao.tool.personalkit.dao;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2024/11/12 21:16
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class RoleDao {

    public List<SysRole> findRoleByUser(String userId) {
        String sql = """
                SELECT
                  r.* 
                FROM
                  sys_role r,
                  sys_user_role sur 
                WHERE
                  r.stat IN ( - 1, 1 ) 
                  AND r.id = sur.role_id 
                  AND sur.user_id = ? 
                """;
        return JdbcUtil.jdbcHelper().findAll(SysRole.class, sql, userId);
    }
}
