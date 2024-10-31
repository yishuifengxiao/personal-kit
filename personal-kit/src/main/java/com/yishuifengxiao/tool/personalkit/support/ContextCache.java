package com.yishuifengxiao.tool.personalkit.support;


import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.tool.utils.ExceptionUtil;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
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

    private final static ThreadLocal<String> currentRole = new ThreadLocal<>();
    private static SysUserDao sysUserDao;

    public static SysUser currentLoginUser() {

        return currentUser().orElseThrow(ExceptionUtil.orElseThrow("当前用户还未登录或已过期"));
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
        SysUser contextUser = GuavaCache.get(authentication.getName(),
                () -> ContextCache.sysUserDao.findActiveSysUser(name).orElse(null));
        return Optional.ofNullable(contextUser);
    }

    public static String currentUserId() {
        return currentLoginUser().getId();
    }

    public ContextCache(SysUserDao sysUserDao) {
        ContextCache.sysUserDao = sysUserDao;
    }

    public static void setRole(String role) {
        currentRole.set(role);
    }

    public static String getRole() {
        return currentRole.get();
    }

    public static void clearRole() {
        currentRole.remove();
    }
}
