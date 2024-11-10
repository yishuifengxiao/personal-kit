package com.yishuifengxiao.tool.personalkit.utils;

import com.yishuifengxiao.common.tool.codec.MD5;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.entity.BoolStat;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2024/11/9 9:38
 * @since 1.0.0
 */
@Component
public class ResourceUtils implements CommandLineRunner {
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    @Value("${server.servlet.context-path:''}")
    private String contextPath;

    @Value("${spring.application.name:''}")
    private String applicationName;

    public void extractAllResources(RequestMappingHandlerMapping requestMappingHandlerMapping1) {
        List<SysPermission> list = new ArrayList<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        handlerMethods.forEach((k, v) -> {
            PathPatternsRequestCondition pathPatternsCondition = k.getPathPatternsCondition();
            Set<String> patternValues = pathPatternsCondition.getPatternValues();
            Set<RequestMethod> methods = k.getMethodsCondition().getMethods();
            Method method = v.getMethod();
            String moduleName = extractModuleName(method.getDeclaringClass());

            Operation apiOperation = AnnotationUtils.findAnnotation(method, Operation.class);
            String summary = null != apiOperation ? apiOperation.summary() : "";
            String description = null != apiOperation ? apiOperation.description() : "";

            PreAuthorize annotation = AnnotationUtils.findAnnotation(method, PreAuthorize.class);
            List<SysPermission> permissions =
                    patternValues.parallelStream().filter(StringUtils::isNotBlank).map(url -> methods.stream().map(s -> {

                        SysPermission permission = new SysPermission(IdWorker.snowflakeStringId(), moduleName,
                                summary,
                                description, url, contextPath, applicationName,
                                method.getDeclaringClass().getName() + "#" + method.getName(),
                                BoolStat.True.code());
                        permission.setId(MD5.md5Short(permission.getApplicationName() + permission.getContextPath() + permission.getUrl()));

                        return permission;
                    }).collect(Collectors.toList())).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
            list.addAll(permissions);
        });
        System.out.println(list);
        System.out.println("--------------------------------");

    }

    private String extractModuleName(Class controller) {
        Tag tag = AnnotationUtils.findAnnotation(controller, Tag.class);
        if (null != tag) {
            return tag.name();
        }
        Schema api = AnnotationUtils.findAnnotation(controller, Schema.class);
        String moduleName = null;
        if (null != api) {
            moduleName = api.name();
            if (StringUtils.isBlank(moduleName)) {
                moduleName = CollUtil.stream(api.types()).collect(Collectors.joining(","));
            }
        }
        return moduleName;
    }

    @Override
    public void run(String... args) throws Exception {
        extractAllResources(null);
    }
}
