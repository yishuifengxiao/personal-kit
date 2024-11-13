package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.guava.EventPublisher;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.security.support.SecurityEvent;
import com.yishuifengxiao.common.security.support.Strategy;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.utils.TokenUtil;
import com.yishuifengxiao.common.support.SpringContext;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.codec.DES;
import com.yishuifengxiao.common.tool.codec.MD5;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.entity.StringKeyValue;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.common.tool.utils.ValidateUtils;
import com.yishuifengxiao.tool.personalkit.dao.RoleDao;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.dao.repository.SysUserRepository;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUserRole;
import com.yishuifengxiao.tool.personalkit.domain.enums.UserStat;
import com.yishuifengxiao.tool.personalkit.domain.query.LoginQuery;
import com.yishuifengxiao.tool.personalkit.domain.query.UserQuery;
import com.yishuifengxiao.tool.personalkit.domain.request.ResetPwdReq;
import com.yishuifengxiao.tool.personalkit.domain.request.UpdatePwdReq;
import com.yishuifengxiao.tool.personalkit.domain.request.UserCreateReq;
import com.yishuifengxiao.tool.personalkit.domain.request.UserRoleReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.CurrentUser;
import com.yishuifengxiao.tool.personalkit.domain.vo.PageUser;
import com.yishuifengxiao.tool.personalkit.listener.event.UserCreateEvent;
import com.yishuifengxiao.tool.personalkit.security.SimpleUserDetailsService;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    @Autowired
    private EventPublisher eventPublisher;

    public SecurityToken login(HttpServletRequest request, HttpServletResponse response,
                               LoginQuery query) throws CustomException {

        try {
            UserDetails details = userDetailsService.loadUserByUsername(query.getUsername().trim());
            Assert.isTrue("密码错误", passwordEncoder.matches(query.getPassword(),
                    details.getPassword()));
            //获取token
            SecurityToken token = TokenUtil.createUnsafe(request, query.getUsername().trim());
            TokenUtil.setToken(token);
            SpringContext.publishEvent(new SecurityEvent(this, request, response,
                    Strategy.AUTHENTICATION_SUCCESS, token, null));
            return token;
        } catch (Exception e) {
            SpringContext.publishEvent(new SecurityEvent(this, request, response,
                    Strategy.AUTHENTICATION_FAILURE, new SecurityToken(query.getUsername(), null,
                    null, null), e));
            throw e;
        }


    }

    public SysUser userInfo(String id) {
        SysUser sysUser = sysUserRepository.findById(id).orElseThrow(() -> UncheckedException.of(
                "记录不存在"));
        return sysUser;
    }

    public void updatePwd(HttpServletRequest request, UpdatePwdReq req) {
        SysUser sysUser =
                sysUserRepository.findById(req.getId().trim()).orElseThrow(() -> UncheckedException.of("记录不存在"));
        Assert.isTrue("旧密码不正确", StringUtils.equals(DES.encrypt(sysUser.getSalt(),
                req.getOldPwd().trim()), sysUser.getPwd()));
        sysUser.setPwd(DES.encrypt(sysUser.getSalt(), req.getNewPwd().trim())).setLastUpdateTime(LocalDateTime.now());
        sysUserRepository.saveAndFlush(sysUser);
        // 删除所有的token
        TokenUtil.clearAllAuthentication();
    }

    public void updateUser(SysUser sysUser) {
        SysUser user =
                sysUserRepository.findById(sysUser.getId().trim()).orElseThrow(ValidateUtils.orElseThrow("记录不存在"));
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
        if (UserStat.SYSTEM_INIT.equalCode(sysUser.getStat())) {
            sysUser.setStat(null);
        }
        if (null != sysUser.getStat()) {
            UserStat.code(sysUser.getStat()).orElseThrow(ValidateUtils.orElseThrow("请选择一个正确的状态"));
            user.setStat(sysUser.getStat());
        }
        user.setLastUpdateTime(LocalDateTime.now());
        JdbcUtil.jdbcHelper().saveOrUpdate(user);
    }

    public void updateResetPwd(HttpServletRequest request, ResetPwdReq req) {
        SysUser sysUser =
                sysUserDao.findActiveSysUser(req.getUsername().trim()).orElseThrow(() -> new UsernameNotFoundException(String.format("用户名%s不存在", req.getUsername().trim())));
        Assert.isTrue("邮箱不匹配", StringUtils.equalsIgnoreCase(sysUser.getEmail(), req.getEmail()));
        sysUser.setPwd(DES.encrypt(sysUser.getSalt(), Constant.DEFAULT_PWD)).setLastUpdateTime(LocalDateTime.now());
        sysUserRepository.saveAndFlush(sysUser);
        // 删除所有的token
        TokenUtil.clearAllAuthentication();
    }


    public Page<PageUser> findPage(PageQuery<UserQuery> query) {


        Page<SysUser> page = sysUserDao.findPage(query);
        return page.map(s -> {
            PageUser pageUser = BeanUtil.copy(s, new PageUser());
            List<StringKeyValue> roles =
                    roleDao.findRoleByUser(s.getId()).stream().map(r -> new StringKeyValue<>(String.valueOf(r.getId()), r.getName())).collect(Collectors.toList());
            pageUser.setRoles(roles);
            return pageUser;
        });

    }

    public CurrentUser findCurrentUser() {
        SysUser sysUser = ContextCache.currentUser().orElseThrow(ValidateUtils.orElseThrow(
                "当前用户还未登录"));

        CurrentUser currentUser = BeanUtil.copy(sysUser, new CurrentUser());

        List<SysRole> roles = roleDao.findRoleByUser(sysUser.getId());

        SysRole sysRole = ContextCache.getRole().orElse(null);

        return currentUser.setRoles(roles).setRole(sysRole);
    }

    public void updateUserRoleReq(UserRoleReq req) {
        SysUser sysUser =
                Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(SysUser.class,
                        req.getId())).orElseThrow(ValidateUtils.orElseThrow("记录不存在"));
        ValidateUtils.isFalse(UserStat.SYSTEM_INIT.equalCode(sysUser.getStat()), "记录不存在");
        JdbcUtil.jdbcHelper().jdbcTemplate().update("DELETE from sys_user_role where user_id=?",
                req.getId());
        req.getRoleIds().stream().map(v -> new SysUserRole(IdWorker.snowflakeStringId(),
                req.getId(), v)).forEach(JdbcUtil.jdbcHelper()::insert);
    }

    public void create(UserCreateReq user) {
        Long count =
                JdbcUtil.jdbcHelper().countAll(new SysUser().setUsername(user.getUsername()).setVer(0), false);
        ValidateUtils.isTrue(null == count || count == 0, "账号已存在");
        Set<String> roles =
                user.getRoleIds().stream().filter(StringUtils::isNotBlank).map(String::trim).collect(Collectors.toSet());
        ValidateUtils.isTrue(!roles.isEmpty(), "角色不能为空");
        for (String roleId : user.getRoleIds()) {
            Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(SysRole.class, roleId)).orElseThrow(ValidateUtils.orElseThrow("角色不存在"));
        }
        String salt = MD5.md5Short(IdWorker.snowflakeStringId());
        String encrypt = DES.encrypt(salt, Constant.DEFAULT_PWD);
        SysUser sysUser = new SysUser(IdWorker.snowflakeStringId(), user.getUsername(),
                user.getNickname(), user.getPhone(), user.getEmail(), user.getCertNo(), salt,
                encrypt, UserStat.ACCOUNT_ENABLE.code(), LocalDateTime.now(), null, 0,
                LocalDateTime.now());
        JdbcUtil.jdbcHelper().insert(sysUser);

        eventPublisher.post(new UserCreateEvent(sysUser));
    }


}
