package com.yishuifengxiao.tool.personalkit.security;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceConfigurator;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.entity.BoolStat;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.access.expression.DefaultHttpSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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
@Order()
public class SimpleCustomResourceConfigurator implements CustomResourceConfigurator {
    @Autowired
    private SysUserDao sysUserDao;


    @Value("${server.servlet.context-path:''}")
    private String contextPath;
    @Autowired
    private ApplicationContext context;


    @Override
    public AuthorizationDecision check(Supplier<Authentication> supplier, RequestAuthorizationContext object) {


        //包含context-path
        HttpServletRequest request = object.getRequest();

        Authentication authentication = supplier.get();
//        if (null != authentication && AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
//            return new AuthorizationDecision(true);
//        }


        AuthorizationDecision decision = authorizationDecision(supplier, object);
        if (null != decision) {
            return decision;
        }

        SysUser sysUser =
                sysUserDao.findActiveSysUser(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException(String.format("账号%s不存在", authentication.getName())));
        // 内置账号默认通过校验
        if (StringUtils.equalsIgnoreCase(sysUser.getId(), Constant.DEFAULT_ROOT_ID) || BoolStat.isTrue(sysUser.getEmbedded())) {
            return new AuthorizationDecision(true);
        }
        //当前角色
        String currentRole = ContextCache.getRole();

        String sql = "SELECT DISTINCT sp.* FROM sys_permission sp, sys_menu_permission smp, sys_menu sm, " +
                "sys_role_menu srm WHERE smp.permission_id = sp.id AND smp.menu_id = sm.id AND sm.id = srm.menu_id ";

        if (StringUtils.isNotBlank(currentRole)) {
            sql += " AND srm.menu_id = " + currentRole;
        }

        List<SysPermission> list = JdbcUtil.jdbcHelper(context).findAll(SysPermission.class, sql);


        OrRequestMatcher orRequestMatcher =
                new OrRequestMatcher(list.stream().map(v -> new AntPathRequestMatcher(v.getUrl())).collect(Collectors.toList()));


        boolean anyMatch = orRequestMatcher.matches(request);

        return new AuthorizationDecision(anyMatch);
    }


    private AuthorizationDecision authorizationDecision(Supplier<Authentication> supplier,
                                                        RequestAuthorizationContext object) {
        Authentication authentication = supplier.get();

        DefaultHttpSecurityExpressionHandler defaultHttpSecurityExpressionHandler =
                new DefaultHttpSecurityExpressionHandler();
        WebSecurityExpressionRoot webSecurityExpressionRoot = new WebSecurityExpressionRoot(supplier,
                object.getRequest());
        EvaluationContext evaluationContext = defaultHttpSecurityExpressionHandler.createEvaluationContext(supplier,
                object);
        // 使用 SpEL 解析表达式
        ExpressionParser parser = new SpelExpressionParser();


        HttpServletRequest request = object.getRequest();
        String uri = request.getRequestURI();
        uri = StringUtils.substringAfter(uri, this.contextPath);
        SysPermission permission = JdbcUtil.jdbcHelper(context).findOne(new SysPermission().setUrl(uri),
                false);
        if (null == permission) {
            return null;
        }
        try {

            String[] tokens = StringUtils.splitByWholeSeparatorPreserveAllTokens(permission.getPath(), "::");
            String preAuthorize =
                    Arrays.stream(Class.forName(tokens[0]).getMethods()).filter(v -> StringUtils.equals(v.getName(),
                            tokens[1])).findFirst().map(method -> AnnotationUtils.findAnnotation(method,
                            PreAuthorize.class)).map(PreAuthorize::value).orElse(null);
            if (StringUtils.isNotBlank(preAuthorize)) {

                Boolean value = parser.parseExpression(preAuthorize).getValue(evaluationContext, Boolean.class);
                if (null != value) {
                    return new AuthorizationDecision(value);
                }
            }

        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public RequestMatcher requestMatcher() {
        try {
            String sql = StringUtils.isBlank(contextPath) ?
                    "SELECT DISTINCT sp.url FROM sys_permission sp WHERE ISNULL" + "(sp.context_path)" : String.format(
                    "SELECT DISTINCT sp.url FROM sys_permission sp WHERE sp.context_path='%s'", contextPath);
            List<String> list = JdbcUtil.jdbcHelper(context).findAll(String.class, sql);
            if (CollUtil.isEmpty(list)) {
                return null;
            }
            return new OrRequestMatcher(list.stream().map(AntPathRequestMatcher::new).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        return new NegatedRequestMatcher(new OrRequestMatcher(propertyResource.permitAll(), propertyResource
//        .anonymous()));

        return AnyRequestMatcher.INSTANCE;
    }


}
