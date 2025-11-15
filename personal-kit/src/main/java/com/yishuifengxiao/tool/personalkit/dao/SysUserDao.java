package com.yishuifengxiao.tool.personalkit.dao;

import com.yishuifengxiao.common.jdbc.JdbcHelper;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.dao.repository.SysUserRepository;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.enums.UserStat;
import com.yishuifengxiao.tool.personalkit.domain.query.UserQuery;
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
        Optional<SysUser> optional =
                jdbcHelper.findAll(new SysUser().setUsername(username.trim()).setVer(0), false)
                        .stream().max(Comparator.comparing(SysUser::getCreateTime));

        return optional;
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

    public Page<SysUser> findPage(PageQuery<UserQuery> query, List<String> roleIds) {
        List<String> roles = null == roleIds ? null : roleIds.stream().filter(StringUtils::isNotBlank).toList();
        UserQuery userQuery = query.query().orElse(new UserQuery());
        UserStat userStat = UserStat.code(userQuery.getStat()).orElse(null);
        Page<SysUser> page = JdbcUtil.jdbcHelper().find(SysUser.class, query, params -> {
            String sql = """
                    SELECT u.* from sys_user u  where u.ver=0
                    """;
            if (StringUtils.isNotBlank(userQuery.getUsername())) {
                sql += " AND u.username LIKE  concat('%', :username, '%')";
                params.put("username", userQuery.getUsername());
            }
            if (StringUtils.isNotBlank(userQuery.getNickname())) {
                sql += " AND u.nickname LIKE  concat('%', :nickname, '%')";
                params.put("nickname", userQuery.getNickname());
            }
            if (StringUtils.isNotBlank(userQuery.getPhone())) {
                sql += " AND u.phone LIKE  concat('%', :phone, '%')";
                params.put("phone", userQuery.getPhone());
            }
            if (StringUtils.isNotBlank(userQuery.getEmail())) {
                sql += " AND u.email LIKE  concat('%', :email, '%')";
                params.put("email", userQuery.getEmail());
            }
            if (StringUtils.isNotBlank(userQuery.getCertNo())) {
                sql += " AND u.cert_no LIKE  concat('%', :certNo, '%')";
                params.put("certNo", userQuery.getCertNo());
            }
            if (null != userStat) {
                sql += " AND u.stat = " + userStat.code();
            }

            if (null != userQuery.getStartCreateTime()) {
                sql += " AND u.create_time >= :startCreateTime";
                params.put("startCreateTime", userQuery.getStartCreateTime());
            }
            if (null != userQuery.getEndCreateTime()) {
                sql += " AND u.create_time <= :endCreateTime";
                params.put("endCreateTime", userQuery.getEndCreateTime());
            }
            if (null != userQuery.getStartLockTime()) {
                sql += " AND u.lock_time >= :startLockTime";
                params.put("startLockTime", userQuery.getStartLockTime());
            }
            if (null != userQuery.getEndLockTime()) {
                sql += " AND u.lock_time <= :endLockTime";
                params.put("endLockTime", userQuery.getEndLockTime());
            }

            if (CollUtil.isNotEmpty(roles)) {
                sql += " and u.id in (SELECT r.user_id from sys_user_role r where r.role_id in (:roleIds))";
                params.put("roleIds", roles);
            }


            return sql;
        });

        return page;
    }
}
