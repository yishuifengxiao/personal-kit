package com.yishuifengxiao.tool.personalkit.dao;

import com.yishuifengxiao.common.jdbc.JdbcHelper;
import com.yishuifengxiao.tool.personalkit.dao.repository.SysUserRepository;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/6-17:05
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class SysUserDao {


    private final SysUserRepository sysUserRepository;

    private final JdbcHelper jdbcHelper;


    public Optional<SysUser> findActiveSysUser(String username) {
        if (StringUtils.isBlank(username)) {
            return Optional.empty();
        }
//        return sysUserRepository.findAll(Example.of(new SysUser().setUsername(username.trim()))
//        , Sort.by(ClassUtil
//        .pojoFieldName(SysUser::getCreateTime)).descending()).stream().findFirst();
        Optional<SysUser> optional = jdbcHelper.findAll(new SysUser().setUsername(username.trim()).setVer(0), false)
                .stream().max(Comparator.comparing(SysUser::getCreateTime));

        return optional;
    }

    public List<SysRole> findAllRoleByUserId(String userId) {
        String sql = """
                SELECT
                	r.*  
                FROM
                	sys_user_role ur,
                	sys_role r  
                WHERE
                	r.id = ur.role_id  
                	AND r.stat IN (- 1, 1 )  
                	AND ur.user_id = ?
                """;
        return jdbcHelper.findAll(SysRole.class, sql, userId);

    }

    public void updateDisableTime(String id, LocalDateTime time) {
        String sql = "update sys_user set lock_time = ? where id = ?";
        jdbcHelper.jdbcTemplate().execute(sql, (PreparedStatementCallback) ps -> {
            ps.setObject(1, time);
            ps.setObject(2, id);
            return ps.executeBatch();
        });
    }

    public String findUserNameById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return sysUserRepository.findById(id.trim()).map(SysUser::getNickname).orElse(null);
    }
}
