package com.yishuifengxiao.tool.personalkit.support.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.yishuifengxiao.common.tool.entity.RootEnum;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.IOException;
import java.lang.reflect.Field;

public class DynamicAddressSerializer extends JsonSerializer<Object> {
    @Override
    public void serialize(Object object, JsonGenerator jsonGenerator,
                          com.fasterxml.jackson.databind.SerializerProvider serializers) throws IOException {
        System.out.println("------------- " + object.getClass().getName());
        System.out.println("----------- " + object);
        DynamicSerialize annotation1 = AnnotationUtils.findAnnotation(object.getClass(), DynamicSerialize.class);
        System.out.println(annotation1);
        // 获取 Address 中的字段
        Field[] fields = object.getClass().getDeclaredFields();
        boolean flag = false;
        for (Field field : fields) {
            // 如果字段上有 DynamicSerialize 注解  
            if (field.isAnnotationPresent(DynamicSerialize.class)) {
                flag = true;
                DynamicSerialize annotation = field.getAnnotation(DynamicSerialize.class);
                Class<? extends RootEnum> value = annotation.value();
                try {
                    RootEnum rootEnum = value.getDeclaredConstructor().newInstance();
                    RootEnum val =
                            rootEnum.vals().stream().filter(s -> s.code().equals(object)).findFirst().orElse(null);
                    jsonGenerator.writeStartObject();
                    // 根据注解中配置的输出字段名进行动态序列化
                    jsonGenerator.writeObjectField(field.getName(), object);
                    jsonGenerator.writeObjectField(field.getName() + "Code", val.code());
                    jsonGenerator.writeObjectField(field.getName() + "Name", val.description());
                    jsonGenerator.writeEndObject();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            }
        }
        if (!flag) {
            jsonGenerator.writeStartObject();
            // 根据注解中配置的输出字段名进行动态序列化
            jsonGenerator.writePOJO(object);
            jsonGenerator.writeEndObject();
        }
    }
}  