package com.yishuifengxiao.tool.personalkit.interceptor;

import com.google.common.eventbus.EventBus;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.tool.personalkit.domain.bo.RequestLogEvent;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;import org.springframework.web.multipart.MultipartFile;

import java.util.List;import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Aspect
@Component
public class MappingAspect {
    @Autowired
    private EventBus asyncEventBus;


    /**
     * 定义切点，匹配所有 @RestController 或 @Controller 注解的类中带有 @GetMapping 或 @PostMapping 注解的方法
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || "//
            + " within(@org" + ".springframework.stereotype.Controller *)") //
    public void controllerMethods() {
    }

    // 定义切点，匹配所有带有 @GetMapping 或 @PostMapping 注解的方法  
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +//
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +//
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +//
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +//
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")//
    public void mappingMethods() {
    }

    // 综合两个切点  
    @Pointcut("controllerMethods() && mappingMethods()")
    public void controllerMappingMethods() {
    }

    // 环绕通知，执行目标方法前后  
    @Around("controllerMappingMethods()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法名和参数
        // 获取 HttpServletRequest
        // 获取 HttpServletRequest 和 HttpServletResponse
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null == attributes ? null : attributes.getRequest();
        HttpServletResponse response = null == attributes ? null : attributes.getResponse();
        String uri = null == request ? null : request.getRequestURI();

        List<Object> params =
                DataUtil.stream(joinPoint.getArgs()).filter(Objects::nonNull).filter(this::isNotSystemParams).collect(Collectors.toList());
        Throwable ex = null;
        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
        } catch (Throwable e) {
            // 打印返回结果
            ex = e;
            throw e;
        } finally {
            SysUser sysUser = ContextCache.currentUser().orElse(null);
            asyncEventBus.post(new RequestLogEvent(request, response, sysUser, params,
                    result, ex));
        }
        return result;
    }

    @PostConstruct
    public void init() {
        asyncEventBus.register(this);
    }

    private boolean isNotSystemParams(Object arg) {

        if (null == arg) {
            return true;
        }
        if (arg instanceof ServletRequest) {
            return false;
        }
        if (arg instanceof ServletResponse) {
            return false;
        }
        if (arg instanceof BindingResult) {
            return false;
        }
        if(arg instanceof MultipartFile){
            return false;
        }
        if(arg instanceof MultipartFile[]){
            return false;
        }
        return true;
    }
}