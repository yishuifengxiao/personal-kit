package com.yishuifengxiao.tool.personalkit;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.tool.personalkit.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.Date;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/10/31-13:20
 * @since 1.0.0
 */
@Slf4j
@SpringBootApplication
@EnableMethodSecurity
@EnableWebSecurity
@EnableAspectJAutoProxy
public class App extends SpringBootServletInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {


        return builder.sources(App.class);

    }

    public static void main(String[] args) {
//        AbstractPreAuthenticatedProcessingFilter.class

//        AbstractPreAuthenticatedProcessingFilter.class
//        AuthorizationManagerBeforeMethodInterceptor.class
//        PreAuthenticatedAuthenticationToken.class;
//        AbstractPreAuthenticatedProcessingFilter.class; //
//        PreAuthenticatedAuthenticationProvider.class
//        org.springframework.security.web.access.intercept.AuthorizationFilter.class
//        AbstractSecurityInterceptor
        ConfigurableApplicationContext context = SpringApplication.run(App.class, args);
        JdbcUtil.jdbcHelper(context);
        runAfterStart(context);
        log.info("=============》 启动成功 {}", new Date());

    }

    private static void runAfterStart(ConfigurableApplicationContext context) {
        ConfigurableEnvironment env = context.getEnvironment();
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        String prefix = StringUtils.isBlank(contextPath) ? "" : contextPath.trim();
        IpUtils.localIps().forEach(ip -> {
            log.info("=============》 在线文档地址 {}", new StringBuilder("http://").append(ip).append(
                    ":").append(serverPort).append(prefix).append("/doc.html"));
        });
    }

}
