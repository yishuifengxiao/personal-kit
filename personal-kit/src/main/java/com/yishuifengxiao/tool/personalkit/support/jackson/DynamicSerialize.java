package com.yishuifengxiao.tool.personalkit.support.jackson;

import com.yishuifengxiao.common.tool.entity.RootEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE,
        ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicSerialize {

    Class<? extends RootEnum> value();
}
