package com.yishuifengxiao.tool.personalkit.support;


import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class ContextCache {

    private final static ThreadLocal<String> currentRole = new ThreadLocal<>();
    private static SysUserDao sysUserDao;

    public static SysUser currentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.isNotNull("当前用户还未登录或登录状态已过期", authentication);
        SysUser contextUser = GuavaCache.get(authentication.getName(), () -> ContextCache.sysUserDao.findActiveSysUser(authentication.getName()).orElse(null));
        Assert.isNotNull("当前用户还未登录或登录状态已过期", contextUser);
        return contextUser;
    }

    public static String currentUserId() {
        return currentUser().getId();
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
