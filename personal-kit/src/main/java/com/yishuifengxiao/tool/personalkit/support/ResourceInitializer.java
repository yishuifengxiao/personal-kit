package com.yishuifengxiao.tool.personalkit.support;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.codec.MD5;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.entity.BoolStat;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.entity.*;
import com.yishuifengxiao.tool.personalkit.domain.enums.RoleStat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.*;
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
    private boolean hasInit = false;
    private final static List<String> sets = Arrays.asList("springfox.documentation", "org.springframework", "org" +
            ".springdoc", "org.springframework.boot");


    @Autowired
    private ApplicationContext context;

    @Value("${server.servlet.context-path:''}")
    private String contextPath;

    @Value("${spring.application.name:''}")
    private String applicationName;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void doInit() {
        if (this.hasInit) {
            return;
        }
        Long userNum = JdbcUtil.jdbcHelper().countAll(new SysUser().setUsername(Constant.DEFAULT_USER), false);
        if (null != userNum && userNum > 0) {
            return;
        }
        // 初始化用户
        SysUser sysUser = SysUser.ofEmbedded(Constant.DEFAULT_ROOT_ID, Constant.DEFAULT_USER, "系统超级管理员",
                Constant.DEFAULT_PWD);
        JdbcUtil.jdbcHelper().saveOrUpdate(sysUser);
        //初始化角色
        SysRole sysRole = new SysRole(Constant.DEFAULT_ROOT_ID, "系统角色",
                "系统初始化数据," + "内置超级管理员，具有系统全部权限", Constant.DEFAULT_ROOT_ID, DEFAULT_HOME_URL,
                RoleStat.ROLE_ENABLE.getCode(), BoolStat.True.code(), LocalDateTime.now(), 1);
        JdbcUtil.jdbcHelper().saveOrUpdate(sysRole);

        //初始化 用户-角色 关联关系
        SysUserRole userRole = new SysUserRole(MD5.md5Short(sysUser.getId() + sysRole.getId()), sysUser.getId(),
                sysRole.getId());
        JdbcUtil.jdbcHelper().saveOrUpdate(userRole);

        //初始化权限
        List<SysPermission> permissions = this.scanSysPermissions();
        permissions.stream().forEach(JdbcUtil.jdbcHelper()::saveOrUpdate);
        // 初始化 角色-权限 关联
        permissions.stream().map(v -> new SysMenuPermission(MD5.md5Short(sysRole.getId() + v.getId()),
                sysRole.getId(), v.getId())).forEach(JdbcUtil.jdbcHelper()::saveOrUpdate);
        this.hasInit = true;
    }

    public List<SysPermission> scanSysPermissions() {
        Map<String, Object> map = new HashMap();
        map.putAll(context.getBeansWithAnnotation(RestController.class));
        map.putAll(context.getBeansWithAnnotation(Controller.class));

        List<SysPermission> list =
                map.values().stream().filter(Objects::nonNull).filter(v -> !sets.stream().anyMatch(s -> StringUtils.containsIgnoreCase(v.getClass().getPackageName(), s))).map(controller -> {
                    // 得到的是controller
                    String moduleName = extractModuleName(controller);
                    List<String> classUrls = extractClassUrls(controller);
                    classUrls = CollUtil.isEmpty(classUrls) ? Arrays.asList("") : classUrls;
                    //方法
                    return classUrls.stream().map(classUrl -> Arrays.stream(controller.getClass().getMethods()).filter(m -> Modifier.isPublic(m.getModifiers())).map(declaredMethod -> {
                        String[] methodPaths = methodPath(declaredMethod);
                        if (CollUtil.isEmpty(methodPaths)) {
                            return null;
                        }
                        Operation apiOperation = AnnotationUtils.findAnnotation(declaredMethod, Operation.class);
                        String summary = null != apiOperation ? apiOperation.summary() : "";
                        String description = null != apiOperation ? apiOperation.description() : "";

                        return Arrays.asList(methodPaths).stream().map(methodUrl -> {
                            String url = StringUtils.trim(classUrl + methodUrl);
                            SysPermission permission = new SysPermission(IdWorker.snowflakeStringId(), moduleName,
                                    summary,
                                    description, url, contextPath, applicationName, BoolStat.True.code());
                            permission.setId(MD5.md5Short(permission.getApplicationName() + permission.getContextPath() + permission.getUrl()));
                            return permission;
                        }).collect(Collectors.toList());
                    }).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList())).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
                }).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
        return list;
    }

    private List<String> extractClassUrls(Object controller) {
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(controller.getClass(), RequestMapping.class);
        if (null != requestMapping) {
            String[] value = requestMapping.value();
            if (CollUtil.isNotEmpty(value)) {
                return Arrays.asList(value);
            }
        }
        return Collections.EMPTY_LIST;
    }

    private String extractModuleName(Object controller) {
        Tag tag = controller.getClass().getAnnotation(Tag.class);
        if (null != tag) {
            return tag.name();
        }
        Schema api = controller.getClass().getAnnotation(Schema.class);
        String moduleName = null;
        if (null != api) {
            moduleName = api.name();
            if (StringUtils.isBlank(moduleName)) {
                moduleName = CollUtil.stream(api.types()).collect(Collectors.joining(","));
            }
        }
        return moduleName;
    }

    private String[] methodPath(Method method) {

        GetMapping methodGetMapping = AnnotationUtils.findAnnotation(method, GetMapping.class);
        if (null != methodGetMapping) {
            return methodGetMapping.value();
        }
        PostMapping methodPostMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
        if (null != methodPostMapping) {
            return methodPostMapping.value();
        }
        DeleteMapping methodDeleteMapping = AnnotationUtils.findAnnotation(method, DeleteMapping.class);
        if (null != methodDeleteMapping) {
            return methodDeleteMapping.value();
        }
        PutMapping methodPutMapping = AnnotationUtils.findAnnotation(method, PutMapping.class);
        if (null != methodPutMapping) {
            return methodPutMapping.value();
        }
        RequestMapping methodRequestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        if (null != methodRequestMapping) {
            return methodRequestMapping.value();
        }
        return null;
    }


    @Override
    public void run(String... args) throws Exception {
        this.doInit();

    }
}