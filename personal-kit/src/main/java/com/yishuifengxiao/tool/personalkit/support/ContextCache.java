package com.yishuifengxiao.tool.personalkit.support;


import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.security.token.extractor.SecurityValueExtractor;
import com.yishuifengxiao.common.tool.utils.ValidateUtils;
import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class ContextCache {
    public ContextCache(SecurityValueExtractor securityValueExtractor) {
        ContextCache.securityValueExtractor = securityValueExtractor;
    }

    private static SecurityValueExtractor securityValueExtractor;

    private final static String USER_PREFIX = "CURRENT_USER_PREFIX::";
    private final static String USER_ROLE_PREFIX = "USER_ROLE_PREFIX::";


    public static SysUser currentLoginUser() {

        return currentUser().orElseThrow(ValidateUtils.orElseThrow("当前用户还未登录或已过期"));
    }

    public static Optional<SysUser> currentUser(Authentication authentication) {
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

    public static Optional<SysUser> currentUser() {
        final Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return currentUser(authentication);
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


    public static void setRole(String username, SysRole role) {


        GuavaCache.put(USER_ROLE_PREFIX + username + getDeviceId(), role);
    }

    public static Optional<SysRole> getRole(String username) {

        SysRole role = (SysRole) GuavaCache.get(USER_ROLE_PREFIX + username + getDeviceId());
        return Optional.ofNullable(role);
    }

    public static void clearRole() {
        GuavaCache.remove(USER_ROLE_PREFIX + getDeviceId());
    }

    private static String getDeviceId() {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String deviceId = securityValueExtractor.extractDeviceId(attributes.getRequest(),
                attributes.getResponse());
        return deviceId;
    }
}
