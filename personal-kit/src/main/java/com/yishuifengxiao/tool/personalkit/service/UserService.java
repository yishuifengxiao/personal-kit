package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.security.support.SecurityEvent;
import com.yishuifengxiao.common.security.support.Strategy;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.utils.TokenUtil;
import com.yishuifengxiao.common.support.SpringContext;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.codec.DES;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.entity.StringKeyValue;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.common.tool.utils.ValidateUtils;
import com.yishuifengxiao.tool.personalkit.dao.RoleDao;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.dao.repository.SysUserRepository;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.enums.UserStat;
import com.yishuifengxiao.tool.personalkit.domain.query.LoginQuery;
import com.yishuifengxiao.tool.personalkit.domain.query.UserQuery;
import com.yishuifengxiao.tool.personalkit.domain.request.ResetPwdReq;
import com.yishuifengxiao.tool.personalkit.domain.request.UpdatePwdReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.PageUser;
import com.yishuifengxiao.tool.personalkit.domain.vo.CurrentUser;
import com.yishuifengxiao.tool.personalkit.security.SimpleUserDetailsService;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/9-18:50
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final SysUserDao sysUserDao;
    private final RoleDao roleDao;
    private final SysUserRepository sysUserRepository;

    private final SimpleUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;


    public SecurityToken login(HttpServletRequest request, HttpServletResponse response, LoginQuery query) throws CustomException {

        try {
            UserDetails details = userDetailsService.loadUserByUsername(query.getUsername().trim());
            SysUser sysUser = GuavaCache.get(query.getUsername().trim(), SysUser.class);
            Assert.isTrue("密码错误", passwordEncoder.matches(query.getPassword(), details.getPassword()));
            //获取token
            SecurityToken token = TokenUtil.createUnsafe(request, query.getUsername().trim());
            return token;
        } catch (Exception e) {
            SpringContext.publishEvent(new SecurityEvent(this, request, response, Strategy.AUTHENTICATION_SUCCESS,
                    new SecurityToken(Collections.EMPTY_LIST) {
                        @Override
                        public String getName() {
                            return query.getUsername();
                        }
                    }, e));
            throw e;
        }


    }

    public SysUser userInfo(String id) {
        SysUser sysUser = sysUserRepository.findById(id).orElseThrow(() -> UncheckedException.of("记录不存在"));
        return sysUser;
    }

    public void updatePwd(UpdatePwdReq req) {
        SysUser sysUser = sysUserRepository.findById(req.getId().trim()).orElseThrow(() -> UncheckedException.of(
                "记录不存在"));
        Assert.isTrue("旧密码不正确", StringUtils.equals(DES.encrypt(sysUser.getSalt(), req.getOldPwd().trim()),
                sysUser.getPwd()));
        sysUser.setPwd(DES.encrypt(sysUser.getSalt(), req.getNewPwd().trim()));
        sysUserRepository.saveAndFlush(sysUser);
        GuavaCache.remove(sysUser.getUsername());
    }

    public void updateUser(SysUser sysUser) {
        SysUser user = sysUserRepository.findById(sysUser.getId().trim()).orElseThrow(() -> UncheckedException.of(
                "记录不存在"));
        if (StringUtils.isNotBlank(sysUser.getNickname())) {
            user.setNickname(sysUser.getNickname().trim());
        }
        if (StringUtils.isNotBlank(sysUser.getPhone())) {
            user.setPhone(sysUser.getPhone().trim());
        }
        if (StringUtils.isNotBlank(sysUser.getEmail())) {
            user.setEmail(sysUser.getEmail().trim());
        }
        if (StringUtils.isNotBlank(sysUser.getCertNo())) {
            user.setCertNo(sysUser.getCertNo().trim());
        }
        if (null != sysUser.getStat()) {
            UserStat.code(sysUser.getStat()).orElseThrow(() -> new UncheckedException("请选择一个正确的状态"));
            user.setStat(sysUser.getStat());
        }
        user.setLastUpdateTime(LocalDateTime.now());
        sysUserRepository.saveAndFlush(sysUser);
        GuavaCache.remove(sysUser.getUsername());
    }

    public void updateResetPwd(ResetPwdReq req) {
        SysUser sysUser =
                sysUserDao.findActiveSysUser(req.getUsername().trim()).orElseThrow(() -> new UsernameNotFoundException(String.format("用户名%s不存在", req.getUsername().trim())));
        Assert.isTrue("邮箱不匹配", StringUtils.equalsIgnoreCase(sysUser.getEmail(), req.getEmail()));
        sysUser.setPwd(DES.encrypt(sysUser.getSalt(), Constant.DEFAULT_PWD));
        sysUserRepository.saveAndFlush(sysUser);
        GuavaCache.remove(sysUser.getUsername());
    }


    public Page<PageUser> findPage(PageQuery<UserQuery> query) {
        UserQuery userQuery = query.query().orElse(new UserQuery());
        UserStat userStat = UserStat.code(userQuery.getStat()).orElse(UserStat.SYSTEM_INIT);
        String sql = """
                SELECT
                  u.*          
                FROM
                  sys_user u
                  LEFT JOIN sys_user_role sur ON u.id = sur.user_id
                  LEFT JOIN sys_role r ON r.id = sur.role_id          
                WHERE
                  u.ver = 0 
                """;
        if (StringUtils.isNotBlank(userQuery.getUsername())) {
            sql += " AND u.username LIKE '%" + userQuery.getUsername() + "%'";
        }
        if (StringUtils.isNotBlank(userQuery.getNickname())) {
            sql += " AND u.nickname LIKE '%" + userQuery.getNickname() + "%'";
        }
        if (StringUtils.isNotBlank(userQuery.getPhone())) {
            sql += " AND u.phone LIKE '%" + userQuery.getPhone() + "%'";
        }
        if (StringUtils.isNotBlank(userQuery.getEmail())) {
            sql += " AND u.email LIKE '%" + userQuery.getEmail() + "%'";
        }
        if (UserStat.SYSTEM_INIT.equals(userStat)) {
            sql += " AND u.stat != " + userStat.code();
        } else {
            sql += " AND u.stat = " + userStat.code();
        }
        if (StringUtils.isNotBlank(userQuery.getRoleName())) {
            sql += " AND r.name LIKE '%" + userQuery.getRoleName() + "%'";
        }
        if (null != userQuery.getStartCreateTime()) {
            sql += " AND u.create_time >= '" + userQuery.getCreateTime() + "'";
        }
        if (null != userQuery.getEndCreateTime()) {
            sql += " AND u.create_time <= '" + userQuery.getEndCreateTime() + "'";
        }
        if (null != userQuery.getStartLockTime()) {
            sql += " AND u.lock_time >= '" + userQuery.getLockTime() + "'";
        }
        if (null != userQuery.getEndLockTime()) {
            sql += " AND u.lock_time <= '" + userQuery.getLockTime() + "'";
        }
        Page<SysUser> page = JdbcUtil.jdbcHelper().findPage(SysUser.class, query, sql);
        return page.map(s -> {
            PageUser pageUser = BeanUtil.copy(s, new PageUser());
            List<StringKeyValue> roles =
                    roleDao.findRoleByUser(s.getId()).stream().map(r -> new StringKeyValue<>(String.valueOf(r.getId()),
                            r.getName())).collect(Collectors.toList());
            pageUser.setRoles(roles);
            return pageUser;
        });

    }

    public CurrentUser findCurrentUser() {
        SysUser sysUser = ContextCache.currentUser().orElseThrow(ValidateUtils.orElseThrow("当前用户还未登录"));

        CurrentUser currentUser = BeanUtil.copy(sysUser, new CurrentUser());

        List<SysRole> roles = roleDao.findRoleByUser(sysUser.getId());

        SysRole sysRole = ContextCache.getRole().orElse(null);

        return currentUser.setRoles(roles).setRole(sysRole);
    }
}
