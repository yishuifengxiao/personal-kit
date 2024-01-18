package com.yishuifengxiao.tool.personalkit.support;


import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.user.CurrentUserDetails;
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
public class ContextUser {

    private final static ThreadLocal<String> currentRole = new ThreadLocal<>();
    private static SysUserDao sysUserDao;


    public static SysUser currentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.isNotNull("当前用户还未登录或登录状态已过期", authentication);
        if (authentication instanceof SecurityToken securityToken) {
            if (null != securityToken.getUserDetails() && securityToken.getUserDetails() instanceof CurrentUserDetails details) {
                if (null != details) {
                    return (SysUser) details.getCurrentUser();
                }
            }
        }
        SysUser sysUser = ContextUser.sysUserDao.findActiveSysUser(authentication.getName()).orElse(null);
//        SysUser sysUser = GuavaCache.get(authentication.getName(), () -> ContextUser.sysUserDao.findActiveSysUser
//        (authentication.getName()).orElse(null));
        Assert.isNotNull("当前用户还未登录或登录状态已过期", sysUser);
        return sysUser;
    }

    public static String currentUserId() {
        return currentUser().getId();
    }

    public ContextUser(SysUserDao sysUserDao) {
        ContextUser.sysUserDao = sysUserDao;
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
