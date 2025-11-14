package com.yishuifengxiao.tool.personalkit.aspect;

import java.lang.annotation.*;

/**
 * Trim注解 - 用于处理Controller方法参数中的空字符串替换为null
 * 
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Trim {
    
    /**
     * 是否深度处理对象内部的字符串字段
     * 默认开启深度处理
     */
    boolean deep() default true;
    
    /**
     * 是否处理集合类型中的字符串
     * 默认开启集合处理
     */
    boolean processCollections() default true;
}
