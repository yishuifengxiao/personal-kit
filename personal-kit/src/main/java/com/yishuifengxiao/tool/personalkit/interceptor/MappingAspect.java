package com.yishuifengxiao.tool.personalkit.interceptor;

import com.google.common.eventbus.EventBus;
import com.yishuifengxiao.tool.personalkit.domain.bo.RequestLogEvent;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.ServletRequestHandledEvent;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
public class MappingAspect {

    @Autowired
    private EventBus asyncEventBus;


    private final static Set<String> urls =
            Arrays.asList("/v3/api-docs").stream().collect(Collectors.toSet());

    @Aspect
    @Component
    public class RequestAdviceAspect {
        /**
         * 定义切点，匹配所有 @RestController 或 @Controller 注解的类中带有 @GetMapping 或 @PostMapping 注解的方法
         */
        @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || "//
                + " within(@org.springframework.stereotype.Controller *)") //
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

        @Before("controllerMethods() && mappingMethods()")
        public void beforeRequest(JoinPoint joinPoint) {
            Object[] args = joinPoint.getArgs();
            Object result = null == args ? null :
                    Arrays.stream(args).filter(MappingAspect.this::isNeedCache).collect(Collectors.toList());
            CacheUtils.setRequestCache(result);
            System.err.println("-------------- 输入参数为 " + args);
        }
    }

    @Aspect
    @Component
    public class ResponseAdviceAspect {

        @After("execution(* org.springframework.web.servlet.mvc.method.annotation" +
                ".ResponseBodyAdvice+.beforeBodyWrite(..))")
        public void interceptBeforeBodyWrite(JoinPoint joinPoint) throws Throwable {
            Object[] args = joinPoint.getArgs();
            Object result = null == args ? null :
                    Arrays.stream(args).filter(MappingAspect.this::isNeedCache).collect(Collectors.toList());
            CacheUtils.setResponseCache(result);
            System.err.println("-------------- 输出参数为 " + args);

        }
    }


    @Component
    public class ServletRequestHandledEventListener implements ApplicationListener<ServletRequestHandledEvent> {
        @Override
        public void onApplicationEvent(ServletRequestHandledEvent event) {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Optional<HttpServletRequest> request = Optional.ofNullable(null == attributes ? null
                    : attributes.getRequest());
            Optional<HttpServletResponse> response = Optional.ofNullable(null == attributes ? null :
                    attributes.getResponse());
            String requestUrl = event.getRequestUrl();
            String method = event.getMethod();
            long id = Thread.currentThread().getId();
            String queryString =
                    request.filter(Objects::nonNull).map(HttpServletRequest::getQueryString).orElse(null);
            Map<String, String[]> parameterMap =
                    request.filter(Objects::nonNull).map(ServletRequest::getParameterMap).orElse(null);
            Map<String, String> requestHeaderMap = new HashMap<>();
            request.filter(Objects::nonNull).ifPresent(s -> {
                s.getHeaderNames().asIterator().forEachRemaining(key -> {
                    if (StringUtils.isNotBlank(key)) {
                        String headerVal = request.map(v -> v.getHeader(key)).orElse(null);
                        if (StringUtils.isNotBlank(headerVal)) {
                            requestHeaderMap.put(key, headerVal);
                        }

                    }
                });
            });
            Map<String, String> responseHeaderMap = new HashMap<>();
            response.filter(Objects::nonNull).ifPresent(s -> {
                s.getHeaderNames().stream().forEach(key -> {
                    if (StringUtils.isNotBlank(key)) {
                        String headerVal = request.map(v -> v.getHeader(key)).orElse(null);
                        if (StringUtils.isNotBlank(headerVal)) {
                            responseHeaderMap.put(key, headerVal);
                        }

                    }
                });
            });
            RequestLogEvent logEvent = new RequestLogEvent(id, requestUrl, method, queryString,
                    requestHeaderMap, parameterMap
                    , responseHeaderMap, ContextCache.currentUser().orElse(null));
            asyncEventBus.post(logEvent);
        }
    }

    private boolean isNeedCache(Object object) {
        if (null == object) {
            return false;
        }
        System.out.println(" 当前参数为  class = " + object.getClass() + " object value " + object);
        if (Void.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        if (MultipartFile.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        if (ServletRequest.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        if (ServletResponse.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        return true;
    }

    @PostConstruct
    public void init() {
        asyncEventBus.register(this);
    }
}