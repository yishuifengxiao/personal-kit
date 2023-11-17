package com.yishuifengxiao.tool.personalkit.dao;

import com.yishuifengxiao.common.jdbc.JdbcHelper;
import com.yishuifengxiao.common.tool.bean.ClassUtil;
import com.yishuifengxiao.tool.personalkit.dao.repository.SysRoleRepository;
import com.yishuifengxiao.tool.personalkit.dao.repository.SysUserRepository;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
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
        return sysUserRepository.findAll(Example.of(new SysUser().setUsername(username.trim())), Sort.by(ClassUtil.pojoFieldName(SysUser::getCreateTime)).descending()).stream().findFirst();
    }

    public List<SysRole> findAllRoleByUserId(String userId) {
        String sql = String.format("SELECT r.* from sys_relation_user_role ur ,sys_role r where r.id=ur.role_id and r.stat=1 and ur.user_id=%s", userId);
        return jdbcHelper.query(SysRole.class, sql).orElse(Collections.EMPTY_LIST);

    }
}
