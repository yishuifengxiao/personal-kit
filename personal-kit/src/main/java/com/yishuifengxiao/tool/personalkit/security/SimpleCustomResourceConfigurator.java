package com.yishuifengxiao.tool.personalkit.security;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceConfigurator;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.utils.ValidateUtils;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/14-19:20
 * @since 1.0.0
 */
@Component
@Order()
public class SimpleCustomResourceConfigurator implements CustomResourceConfigurator {
    @Autowired
    private SysUserDao sysUserDao;


    @Value("${spring.application.name:''}")
    private String applicationName;

    @Autowired
    private ApplicationContext context;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> supplier,
                                       RequestAuthorizationContext object) {


        //包含context-path
        HttpServletRequest request = object.getRequest();

        Authentication authentication = supplier.get();
        SysUser sysUser = ContextCache.currentUser(authentication).orElse(null);

        //当前角色
        String currentRole =
                ContextCache.getRole().map(SysRole::getId).orElseThrow(ValidateUtils.orElseThrow(
                        "当前用户还未登录"));

        String sql = """
                SELECT DISTINCT
                  s.url  
                FROM
                  sys_permission s  
                WHERE
                  s.id IN (
                    SELECT
                      smp.permission_id  
                    FROM
                      sys_menu_permission smp,
                      sys_menu sm  
                    WHERE
                      sm.is_show = 1  
                      AND smp.menu_id = sm.id  
                      AND smp.id IN (
                        SELECT
                          srm.role_id  
                        FROM
                          sys_role_menu srm,
                          sys_role sr  
                        WHERE
                          sr.stat IN ( - 1, 1 )  
                          AND srm.role_id = sr.id  
                          AND srm.role_id IN ( :roleIds )  
                      )  
                  )
                                
                """;
        Map map = Map.of("roleIds", Arrays.asList(currentRole));


        List<String> urls = JdbcUtil.jdbcHelper(context).findAll(String.class, sql, map);


        OrRequestMatcher orRequestMatcher =
                new OrRequestMatcher(urls.stream().map(v -> new AntPathRequestMatcher(v)).collect(Collectors.toList()));


        boolean anyMatch = orRequestMatcher.matches(request);

        return new AuthorizationDecision(anyMatch);
    }

    @Override
    public RequestMatcher requestMatcher() {
        try {
            String sql = """
                    SELECT DISTINCT s.url from sys_permission s where s.application_name=?
                    """;
            List<String> list = JdbcUtil.jdbcHelper(context).findAll(String.class, sql,
                    applicationName);
            if (CollUtil.isEmpty(list)) {
                return null;
            }
            return new OrRequestMatcher(list.stream().map(AntPathRequestMatcher::new).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        return new NegatedRequestMatcher(new OrRequestMatcher(propertyResource.permitAll(),
//        propertyResource
//        .anonymous()));

        return AnyRequestMatcher.INSTANCE;
    }


}
