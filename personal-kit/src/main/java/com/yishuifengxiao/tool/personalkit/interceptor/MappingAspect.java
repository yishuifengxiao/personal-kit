package com.yishuifengxiao.tool.personalkit.interceptor;

import com.google.common.eventbus.EventBus;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.tool.personalkit.domain.bo.RequestLogEvent;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.ServletRequestHandledEvent;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
public class MappingAspect {

    @Autowired
    private EventBus asyncEventBus;


    private final static String[] urls = new String[]{".", "/v3/api-docs"};

    private final static String[] keywords = CollUtil.asArray("cookie", "content-length", "sec-",
            ":", "accept", "host", "connection");

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
            Object result = null;
            if (null != args && args.length > 0) {
                List<Object> list =
                        Arrays.stream(args).filter(MappingAspect.this::isNeedCache).collect(Collectors.toList());
                if (list.size() == 1) {
                    result = list.get(0);
                } else {
                    result = list;
                }
            }
            CacheUtils.setRequestCache(result);
        }
    }

    @Aspect
    @Component
    public class ResponseAdviceAspect {

        @AfterReturning(pointcut = "execution(* org.springframework.web.servlet.mvc.method" +
                ".annotation" + ".ResponseBodyAdvice+.beforeBodyWrite(..))", returning = "result")
        public void interceptBeforeBodyWrite(Object result) throws Throwable {
            Object args = result;
            CacheUtils.setResponseCache(result);
            //保存请求时间
            CacheUtils.setRequestTime();
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
            Optional<HttpServletResponse> response = Optional.ofNullable(null == attributes ?
                    null : attributes.getResponse());
            String requestUrl = event.getRequestUrl();
            if (StringUtils.containsAnyIgnoreCase(requestUrl, urls)) {
                return;
            }
            String method = event.getMethod();
            long id = Thread.currentThread().getId();
            String queryString =
                    request.filter(Objects::nonNull).map(HttpServletRequest::getQueryString).orElse(null);
            Map<String, String[]> parameterMap =
                    request.filter(Objects::nonNull).map(ServletRequest::getParameterMap).orElse(null);
            Map<String, String> requestHeaderMap = new HashMap<>();
            request.filter(Objects::nonNull).ifPresent(s -> {
                Collections.list(s.getHeaderNames()).parallelStream().filter(v -> !StringUtils.containsAnyIgnoreCase(v, keywords))
                        .forEach(key -> {
                            if (StringUtils.isNotBlank(key)) {
                                if (!StringUtils.containsAnyIgnoreCase(key, keywords)) {
                                    String headerVal =
                                            request.map(v -> v.getHeader(key)).orElse(null);
                                    if (StringUtils.isNotBlank(headerVal)) {
                                        requestHeaderMap.put(key, headerVal);
                                    }
                                }


                            }
                        });
            });
            Map<String, String> responseHeaderMap = new HashMap<>();
            response.filter(Objects::nonNull).ifPresent(s -> {
                s.getHeaderNames().parallelStream().filter(v -> !StringUtils.containsAnyIgnoreCase(v, keywords))
                        .forEach(key -> {
                            if (StringUtils.isNotBlank(key)) {
                                if (!StringUtils.containsAnyIgnoreCase(key, keywords)) {
                                    String headerVal =
                                            request.map(v -> v.getHeader(key)).orElse(null);
                                    if (StringUtils.isNotBlank(headerVal)) {
                                        requestHeaderMap.put(key, headerVal);
                                    }
                                }


                            }
                        });
            });
            //耗时的毫秒数
            long untiled = CacheUtils.getRequestTime().until(LocalDateTime.now(),
                    ChronoUnit.MILLIS);
            RequestLogEvent logEvent = new RequestLogEvent(id, untiled, requestUrl, method,
                    queryString, requestHeaderMap, parameterMap, responseHeaderMap,
                    ContextCache.currentUser().orElse(null));
            asyncEventBus.post(logEvent);
        }
    }

    private boolean isNeedCache(Object object) {
        if (null == object) {
            return false;
        }

        boolean flag = true;
        if (Void.class.isAssignableFrom(object.getClass())) {
            flag = false;
        } else if (StringUtils.startsWithIgnoreCase(object.getClass().getName(), "org" +
                ".springframework")) {
            flag = false;
        } else if (MultipartFile.class.isAssignableFrom(object.getClass())) {
            flag = false;
        } else if (ServletRequest.class.isAssignableFrom(object.getClass())) {
            flag = false;
        } else if (ServletResponse.class.isAssignableFrom(object.getClass())) {
            flag = false;
        } else if (Filter.class.isAssignableFrom(object.getClass())) {
            flag = false;
        } else if (BeanPropertyBindingResult.class.isAssignableFrom(object.getClass())) {
            flag = false;
        } else if (org.springframework.http.MediaType.class.isAssignableFrom(object.getClass())) {
            flag = false;
        }
        return flag;
    }

    @PostConstruct
    public void init() {
        asyncEventBus.register(this);
    }
}