package com.yishuifengxiao.tool.personalkit.security;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.tool.collections.CollectionUtil;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import com.yishuifengxiao.tool.personalkit.tool.ResourceInitializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
    private PropertyResource propertyResource;
    @Autowired
    private ResourceInitializer resourceInitializer;

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        //包含context-path
        String uri = request.getRequestURI().toLowerCase();
        String uriWithoutContextPath = StringUtils.substringAfter(uri, propertyResource.contextPath());

        String sql = "SELECT  DISTINCT sp.* FROM sys_user su JOIN sys_relation_user_role sur ON su.id = sur.user_id " + "JOIN " + "sys_role sr ON sur.role_id = sr.id AND sr.stat = 1" + " JOIN sys_relation_role_permission srp ON sr.id = srp.role_id JOIN sys_permission sp ON srp.permission_id = sp.id " + "WHERE su.username = '%s' AND su.ver = 0";
        sql = String.format(sql, authentication.getName());

        List<SysPermission> list = JdbcUtil.jdbcHelper().query(SysPermission.class, sql).orElse(Collections.EMPTY_LIST);


        OrRequestMatcher orRequestMatcher = new OrRequestMatcher(list.stream().map(v -> new AntPathRequestMatcher(v.getUrl())).collect(Collectors.toList()));


        boolean anyMatch = orRequestMatcher.matches(request);
        return anyMatch;
    }

    @Override
    public RequestMatcher requestMatcher() {
        resourceInitializer.doInit();
        String sql = StringUtils.isBlank(propertyResource.contextPath()) ? "SELECT DISTINCT sp.url FROM sys_permission sp WHERE ISNULL(sp.context_path)" : String.format("SELECT DISTINCT sp.url FROM sys_permission sp WHERE sp.context_path='%s'", propertyResource.contextPath());
        List<String> list = JdbcUtil.jdbcTemplate().queryForList(sql, String.class);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        Set<String> definedUrls = propertyResource.definedUrls();
        return new OrRequestMatcher(DataUtil.stream(list).filter(v -> !definedUrls.contains(v)).filter(StringUtils::isNotBlank).map(AntPathRequestMatcher::new).collect(Collectors.toList()));
    }

    private RequestMatcher requestMatchers() {
        return new OrRequestMatcher();
    }
}
