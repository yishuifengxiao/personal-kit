package com.yishuifengxiao.tool.personalkit.security;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.entity.BoolStat;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.tool.ResourceInitializer;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/14-19:20
 * @since 1.0.0
 */
@Component
public class SimpleCustomResourceProvider implements CustomResourceProvider {
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private PropertyResource propertyResource;
    @Autowired
    private ResourceInitializer resourceInitializer;


    private String currentRole(HttpServletRequest request) {
        String currentRole = request.getHeader(Constant.CURRENT_ROLE);
        if (StringUtils.isNotBlank(currentRole)) {
            return currentRole.trim();
        }
        return request.getParameter(Constant.CURRENT_ROLE);
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        //包含context-path
        HttpServletRequest request = object.getRequest();
        SysUser sysUser = sysUserDao.findActiveSysUser(authentication.get().getName())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("账号%s不存在", authentication.get().getName())));
        // 内置账号默认通过校验
        if (StringUtils.equalsIgnoreCase(sysUser.getId(), Constant.DEFAULT_ROOT_ID) || BoolStat.isTrue(sysUser.getEmbedded())) {
            return new AuthorizationDecision(true);
        }
        //当前角色
        String currentRole = currentRole(request);

        String sql = "SELECT DISTINCT sp.* FROM sys_permission sp, sys_menu_permission smp, sys_menu sm, " +
                "sys_role_menu srm WHERE smp.permission_id = sp.id AND smp.menu_id = sm.id AND sm.id = srm.menu_id ";

        if (StringUtils.isNotBlank(currentRole)) {
            sql += " AND srm.menu_id = " + currentRole;
        }

        List<SysPermission> list = JdbcUtil.jdbcHelper().query(SysPermission.class, sql).orElse(Collections.EMPTY_LIST);


        OrRequestMatcher orRequestMatcher = new OrRequestMatcher(list.stream().map(v -> new AntPathRequestMatcher(v.getUrl())).collect(Collectors.toList()));


        boolean anyMatch = orRequestMatcher.matches(request);

        return new AuthorizationDecision(anyMatch);
    }

    @Override
    public RequestMatcher requestMatcher() {
        resourceInitializer.doInit();
        String sql = StringUtils.isBlank(propertyResource.contextPath()) ? "SELECT DISTINCT sp.url FROM sys_permission sp WHERE ISNULL(sp.context_path)" : String.format("SELECT DISTINCT sp.url FROM sys_permission sp WHERE sp.context_path='%s'", propertyResource.contextPath());
        List<String> list = JdbcUtil.jdbcTemplate().queryForList(sql, String.class);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
//        return new NegatedRequestMatcher(new OrRequestMatcher(propertyResource.permitAll(), propertyResource.anonymous()));

        return AnyRequestMatcher.INSTANCE;
    }


}
