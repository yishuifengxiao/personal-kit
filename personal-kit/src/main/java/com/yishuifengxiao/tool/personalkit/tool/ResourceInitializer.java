package com.yishuifengxiao.tool.personalkit.tool;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.encoder.Md5;
import com.yishuifengxiao.common.tool.entity.BoolStat;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.tool.personalkit.dao.repository.*;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.entity.*;
import com.yishuifengxiao.tool.personalkit.domain.enums.RoleStat;
import com.yishuifengxiao.tool.personalkit.domain.enums.UserStat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
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
    private final static List<String> sets = Arrays.asList("springfox.documentation", "org.springframework");


    @Autowired
    private ApplicationContext context;

    @Value("${server.servlet.context-path:''}")
    private String contextPath;

    @Value("${spring.application.name:''}")
    private String applicationName;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<SysPermission> scanSysPermissions() {
        List<SysPermission> list = new ArrayList<>();
        Map<String, Object> map = new HashMap();
        map.putAll(context.getBeansWithAnnotation(RestController.class));
        map.putAll(context.getBeansWithAnnotation(Controller.class));
        map.values().stream().filter(Objects::nonNull).filter(v -> !sets.stream().anyMatch(s -> StringUtils.containsIgnoreCase(v.getClass().getPackageName(), s))).forEach(c -> {
            // 得到的是controller
            Api api = c.getClass().getAnnotation(Api.class);
            RequestMapping requestMapping = c.getClass().getAnnotation(RequestMapping.class);
            String[] classUrls = null != requestMapping ? requestMapping.value() : null;
            String moduleName = null;
            if (null != api) {
                moduleName = api.value();
                if (StringUtils.isBlank(moduleName)) {
                    moduleName = DataUtil.stream(api.tags()).collect(Collectors.joining(","));
                }
            }

            Method[] declaredMethods = c.getClass().getMethods();

            assemble(moduleName, classUrls, declaredMethods, list);

        });
        return list;

    }

    private void assemble(String moduleName, String[] classUrls, Method[] declaredMethods, List<SysPermission> list) {
        Arrays.stream(declaredMethods).forEach(m -> {
            String[] methodPaths = methodPath(m);
            if (null != methodPaths) {
                ApiOperation apiOperation = AnnotationUtils.findAnnotation(m, ApiOperation.class);
                String name = null != apiOperation ? apiOperation.value() : "";
                String note = null != apiOperation ? apiOperation.notes() : "";

                if (null != classUrls && classUrls.length > 0) {
                    DataUtil.stream(classUrls).forEach(classUrl -> DataUtil.stream(methodPaths).forEach(methodPath -> this.assemble(list, moduleName, name, note, new StringBuilder(null == classUrl ? "" : classUrl.trim()).append(null == methodPath ? "" : methodPath.trim()).toString())));
                } else {
                    DataUtil.stream(methodPaths).forEach(methodPath -> this.assemble(list, moduleName, name, note, null == methodPath ? "" : methodPath.trim()));
                }


            }

        });
    }

    private void assemble(List<SysPermission> list, String moduleName, String name, String note, String url) {
        SysPermission permission = new SysPermission(IdWorker.snowflakeStringId(), moduleName, name, note, url, contextPath, applicationName, BoolStat.True.code());
        permission.setId(Md5.md5Short(permission.getApplicationName() + permission.getContextPath() + permission.getUrl()));
        list.add(permission);
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

    public void doInit() {
        if (this.hasInit) {
            return;
        }
        Long userNum = JdbcUtil.jdbcHelper().countAll(new SysUser().setUsername(Constant.DEFAULT_USER));
        if (null != userNum && userNum > 0) {
            return;
        }
        //初始化权限
        List<SysPermission> permissions = this.scanSysPermissions();
        permissions.stream().forEach(JdbcUtil.jdbcHelper()::saveOrUpdate);
        //初始化角色
        SysRole sysRole = JdbcUtil.jdbcHelper().saveOrUpdate(new SysRole(Constant.DEFAULT_ROOT_ID, "系统角色", "系统初始化数据,内置超级管理员，具有系统全部权限", Constant.DEFAULT_ROOT_ID, DEFAULT_HOME_URL, RoleStat.ROLE_ENABLE.getCode(), BoolStat.True.code()));
        // 初始化用户
        SysUser sysUser = JdbcUtil.jdbcHelper().saveOrUpdate(new SysUser().setUsername(Constant.DEFAULT_USER).setPwd(passwordEncoder.encode(Constant.DEFAULT_PWD)).setId(Constant.DEFAULT_ROOT_ID).setEmbedded(BoolStat.True.code())
                //
                .setVer(Constant.ACTIVE_DATA_VER).setStat(UserStat.ACCOUNT_ENABLE.getCode()).setCreateTime(LocalDateTime.now()).setLastUpdateTime(LocalDateTime.now()).setNickname("系统超级管理员"));

        // 初始化 角色-权限 关联
        permissions.stream().map(v -> new SysRelationRolePermission(Md5.md5Short(sysRole.getId() + v.getId()), sysRole.getId(), v.getId())).forEach(JdbcUtil.jdbcHelper()::saveOrUpdate);

        //初始化
        SysRelationUserRole userRole = new SysRelationUserRole(Md5.md5Short(sysUser.getId() + sysRole.getId()), sysUser.getId(), sysRole.getId());
        JdbcUtil.jdbcHelper().saveOrUpdate(userRole);
        this.hasInit = true;
    }

    @Override
    public void run(String... args) throws Exception {
        this.doInit();

    }
}