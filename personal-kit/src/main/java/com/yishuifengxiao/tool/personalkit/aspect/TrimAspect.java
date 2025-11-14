package com.yishuifengxiao.tool.personalkit.aspect;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Trim注解切面处理
 *
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
@Aspect
@Component
public class TrimAspect {

    /**
     * 定义切点：匹配所有带有@Trim注解的方法
     */
    @Pointcut("@annotation(com.yishuifengxiao.tool.personalkit.aspect.Trim)")
    public void trimMethodAnnotationPointcut() {
    }

    /**
     * 定义切点：匹配所有带有@Trim注解的类中的public方法
     */
    @Pointcut("@within(com.yishuifengxiao.tool.personalkit.aspect.Trim)")
    public void trimClassAnnotationPointcut() {
    }

    /**
     * 组合切点：方法级别注解或类级别注解
     */
    @Pointcut("trimMethodAnnotationPointcut() || trimClassAnnotationPointcut()")
    public void trimAnnotationPointcut() {
    }

    /**
     * 环绕通知：处理@Trim注解
     */
    @Around("trimAnnotationPointcut()")
    public Object aroundTrimAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名和参数
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();

        // 获取Trim注解配置（优先使用方法级别的注解，如果没有则使用类级别的注解）
        Trim trimAnnotation = getTrimAnnotation(method);

        if (trimAnnotation == null) {
            // 如果没有找到注解，直接执行原方法
            return joinPoint.proceed(args);
        }

        boolean deep = trimAnnotation.deep();
        boolean processCollections = trimAnnotation.processCollections();

        // 处理参数
        Object[] processedArgs = processParameters(args, parameters, deep, processCollections);

        // 使用处理后的参数执行原方法
        return joinPoint.proceed(processedArgs);
    }

    /**
     * 获取Trim注解配置（优先使用方法级别，其次类级别）
     */
    private Trim getTrimAnnotation(Method method) {
        // 首先检查方法级别的注解
        Trim methodAnnotation = method.getAnnotation(Trim.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        // 如果没有方法级别的注解，检查类级别的注解
        Class<?> targetClass = method.getDeclaringClass();
        Trim classAnnotation = targetClass.getAnnotation(Trim.class);
        if (classAnnotation != null) {
            return classAnnotation;
        }

        return null;
    }

    /**
     * 处理参数中的空字符串
     */
    private Object[] processParameters(Object[] args, Parameter[] parameters, boolean deep, boolean processCollections) {
        if (args == null || parameters == null || args.length != parameters.length) {
            return args;
        }

        Object[] processedArgs = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            Parameter parameter = parameters[i];

            // 处理参数值
            processedArgs[i] = processValue(arg, deep, processCollections);
        }

        return processedArgs;
    }

    /**
     * 递归处理值
     */
    private Object processValue(Object value, boolean deep, boolean processCollections) {
        if (value == null) {
            return null;
        }

        // 处理字符串
        if (value instanceof String) {
            String str = (String) value;
            return StringUtils.isBlank(str) ? null : str.trim();
        }

        // 处理集合
        if (processCollections) {
            if (value instanceof Collection) {
                Collection<?> collection = (Collection<?>) value;
                List<Object> processedList = new ArrayList<>();
                for (Object item : collection) {
                    processedList.add(processValue(item, deep, processCollections));
                }
                return processedList;
            }

            if (value instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) value;
                Map<Object, Object> processedMap = new HashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    Object processedKey = processValue(entry.getKey(), deep, processCollections);
                    Object processedValue = processValue(entry.getValue(), deep, processCollections);
                    processedMap.put(processedKey, processedValue);
                }
                return processedMap;
            }
        }

        // 深度处理对象
        if (deep && isCustomObject(value)) {
            return processObjectFields(value, deep, processCollections);
        }

        return value;
    }

    /**
     * 判断是否为自定义对象（非Java原生类型和常用框架类型）
     */
    private boolean isCustomObject(Object obj) {
        if (obj == null) {
            return false;
        }

        Class<?> clazz = obj.getClass();
        String packageName = clazz.getPackage().getName();

        // 排除Java原生类型和常用框架类型
        return !packageName.startsWith("java.") &&
                !packageName.startsWith("javax.") &&
                !packageName.startsWith("org.springframework.") &&
                !packageName.startsWith("org.apache.") &&
                !packageName.startsWith("com.google.");
    }

    /**
     * 递归处理对象的字段
     */
    private Object processObjectFields(Object obj, boolean deep, boolean processCollections) {
        if (obj == null) {
            return null;
        }

        try {
            // 使用反射处理对象的所有字段
            java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                // 跳过静态字段和final字段
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) ||
                        java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);
                Object fieldValue = field.get(obj);
                Object processedValue = processValue(fieldValue, deep, processCollections);

                // 如果值发生了变化，则设置新值
                if (fieldValue != processedValue) {
                    field.set(obj, processedValue);
                }
            }
        } catch (IllegalAccessException e) {
            // 忽略访问异常，继续处理其他字段
        }

        return obj;
    }
}