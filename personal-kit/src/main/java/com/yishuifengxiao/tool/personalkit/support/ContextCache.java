package com.yishuifengxiao.tool.personalkit.support;


import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.tool.utils.ValidateUtils;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class ContextCache {

    private final static String USER_PREFIX = "CURRENT_USER_PREFIX::";
    private final static String USER_ROLE_PREFIX = "USER_ROLE_PREFIX::";


    public static SysUser currentLoginUser() {

        return currentUser().orElseThrow(ValidateUtils.orElseThrow("当前用户还未登录或已过期"));
    }

    public static Optional<SysUser> currentUser() {
        final Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (null == authentication) {
            return Optional.empty();
        }
        String name = authentication.getName();
        if (StringUtils.isBlank(name)) {
            return Optional.empty();
        }
        SysUser sysUser =
                (SysUser) GuavaCache.get(USER_PREFIX + name);
        if (null == sysUser) {
            return Optional.empty();
        }
        return Optional.ofNullable(sysUser);
    }


    public static String currentUserId() {
        return currentLoginUser().getId();
    }

    public static void setCurrentUser(SysUser sysUser) {
        if (null == sysUser) {
            return;
        }
        GuavaCache.put(USER_PREFIX + sysUser.getUsername(),
                sysUser);
    }


    public static void setRole(SysRole role) {
        final Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        GuavaCache.put(USER_ROLE_PREFIX + authentication.getName(), role);
    }

    public static Optional<SysRole> getRole() {
        final Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        SysRole role = (SysRole) GuavaCache.get(USER_ROLE_PREFIX + authentication.getName());
        return Optional.ofNullable(role);
    }

    public static void clearRole() {
        final Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        GuavaCache.remove(USER_ROLE_PREFIX + authentication.getName());
    }
}
