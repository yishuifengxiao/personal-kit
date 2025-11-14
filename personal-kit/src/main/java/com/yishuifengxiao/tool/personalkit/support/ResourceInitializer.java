package com.yishuifengxiao.tool.personalkit.support;

import com.yishuifengxiao.common.guava.EventPublisher;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.support.api.ApiInfo;
import com.yishuifengxiao.common.tool.codec.Md5;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUserRole;
import com.yishuifengxiao.tool.personalkit.domain.enums.RoleStat;
import com.yishuifengxiao.tool.personalkit.listener.event.UserCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.yishuifengxiao.tool.personalkit.domain.constant.Constant.DEFAULT_HOME_URL;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/7-19:37
 * @since 1.0.0
 */
@Component("resourceInitializer")
public class ResourceInitializer implements CommandLineRunner {
    private final static List<String> sets = Arrays.asList("springfox.documentation", "org" +
            ".springframework", "org" + ".springdoc", "org.springframework.boot");


    @Autowired
    private ApplicationContext context;

    @Value("${server.servlet.context-path:''}")
    private String contextPath;

    @Value("${spring.application.name:''}")
    private String applicationName;

    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ApplicationContext applicationContext;

    public void doInit() throws IOException {
        JdbcUtil.jdbcHelper().jdbcTemplate().update("DELETE from sys_permission where application_name=?",
                this.applicationName);
        //初始化权限
        List<SysPermission> permissions = this.scanSysPermissions();
        permissions.stream().forEach(JdbcUtil.jdbcHelper()::saveOrUpdate);
        // 初始化用户
        SysUser sysUser = SysUser.ofEmbedded(Constant.DEFAULT_ROOT_ID, Constant.DEFAULT_USER,
                "系统超级管理员", Constant.DEFAULT_PWD);
        JdbcUtil.jdbcHelper().saveOrUpdate(sysUser);
        //初始化角色
        SysRole sysRole = new SysRole(Constant.DEFAULT_ROOT_ID, "系统角色", "系统初始化数据," +
                "内置超级管理员，具有系统全部权限", Constant.DEFAULT_ROOT_ID, DEFAULT_HOME_URL,
                RoleStat.ROLE_INIT.code(), LocalDateTime.now());
        JdbcUtil.jdbcHelper().saveOrUpdate(sysRole);

        //初始化 用户-角色 关联关系
        SysUserRole userRole = new SysUserRole(Md5.md5Short(sysUser.getId() + sysRole.getId()),
                sysUser.getId(), sysRole.getId());
        JdbcUtil.jdbcHelper().saveOrUpdate(userRole);


        // 初始化角色-菜单关系
        JdbcUtil.jdbcHelper().jdbcTemplate().execute("""
                INSERT IGNORE INTO sys_role_menu( id, role_id, menu_id) SELECT md5( CONCAT( m.id )), 1, m.id FROM sys_menu m
                """);

        // 创建用户的消息
        eventPublisher.post(new UserCreateEvent(sysUser));
    }


    public List<SysPermission> scanSysPermissions() {
        List<ApiInfo> permissions = new com.yishuifengxiao.common.support.ApiEndpointScanner(applicationContext).allApiEndpoints();
        return permissions.stream().map(apiInfo -> {
            SysPermission sysPermission = new SysPermission(null, apiInfo.getModuleName(),
                    apiInfo.getMethodValue(), apiInfo.getMethodDescription(), CollUtil.first(apiInfo.getPath()).get(),
                    CollUtil.first(apiInfo.getHttpMethods()).get(), contextPath, applicationName, 1
            );
            sysPermission.setId(Md5.md5Short(sysPermission.getModule() + sysPermission.getUrl() + sysPermission.getHttpMethod()));
            return sysPermission;

        }).collect(Collectors.toList());
    }


    @Override
    public void run(String... args) throws Exception {
        this.doInit();
    }


}