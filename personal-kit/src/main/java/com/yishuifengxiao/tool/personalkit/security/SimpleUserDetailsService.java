package com.yishuifengxiao.tool.personalkit.security;

import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.security.user.CurrentUserDetails;
import com.yishuifengxiao.common.tool.codec.DES;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.common.tool.utils.ValidateUtils;
import com.yishuifengxiao.tool.personalkit.dao.RoleDao;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.enums.UserStat;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/6-16:02
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class SimpleUserDetailsService implements UserDetailsService {

    private final SysUserDao sysUserDao;
    private final RoleDao roleDao;

    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // @formatter:off
        SysUser sysUser = GuavaCache.get(username, ()-> sysUserDao.findActiveSysUser(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("用户名%s不存在", username))));

        Assert.isFalse("账户已禁用，请稍候一段时候再重试",null!=sysUser.getLockTime()&&sysUser.getLockTime().isAfter(LocalDateTime.now()));

        List<SysRole> roles = roleDao.findRoleByUser(sysUser.getId());
        ValidateUtils.isTrue(CollUtil.isNotEmpty(roles),"当前用户还未配置角色");

        SysRole role = currentRole(roles);
        ContextCache.setRole(sysUser.getUsername(),role);
        String password=passwordEncoder.encode(DES.decrypt(sysUser.getSalt(),sysUser.getPwd()));
        ContextCache.setCurrentUser(sysUser);

        return new CurrentUserDetails(username, password,
               ! UserStat.ACCOUNT_DISABLE.equalCode( sysUser.getStat()),
                ! UserStat.ACCOUNT_EXPIRED.equalCode( sysUser.getStat()),
                ! UserStat.CREDENTIALS_EXPIRED.equalCode( sysUser.getStat()) ,
                ! UserStat.ACCOUNT_LOCKED.equalCode( sysUser.getStat()),
                AuthorityUtils.commaSeparatedStringToAuthorityList(
                        CollUtil.stream(roles).map(SysRole::getName)
                                .filter(StringUtils::isNotBlank)
                                .distinct().collect(Collectors.joining(","))))
                .setCurrentUser(sysUser);
        // @formatter:on
    }

    private SysRole currentRole(List<SysRole> roles) {
        if (null == roles || roles.isEmpty()) {
            return null;
        }
        SysRole role = null;
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String currentRole = request.getHeader(Constant.CURRENT_ROLE);
            if (StringUtils.isBlank(currentRole)) {
                currentRole = request.getParameter(Constant.CURRENT_ROLE);
            }
            String loginRole = currentRole;

            if (StringUtils.isNotBlank(loginRole)) {
                role = roles.stream().filter(Objects::nonNull).filter(v -> StringUtils.equals(v.getName(), loginRole)).findFirst().orElse(roles.stream().filter(Objects::nonNull).filter(v -> StringUtils.equals(String.valueOf(v.getId()), loginRole)).findFirst().orElse(null));

            } else {
                role = roles.get(0);
            }
        } catch (Exception e) {
        }
        return role;

    }
}
