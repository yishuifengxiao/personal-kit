package com.yishuifengxiao.tool.personalkit.interceptor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.eventbus.Subscribe;
import com.yishuifengxiao.common.guava.EventPublisher;
import com.yishuifengxiao.common.jdbc.JdbcHelper;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.tool.personalkit.domain.bo.RequestLogEvent;
import com.yishuifengxiao.tool.personalkit.domain.entity.HttpLog;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Component
public class RequestLogEventListener {
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private JdbcHelper jdbcHelper;

    @Subscribe
    public void onRequestLogEvent(RequestLogEvent event) {

        String userId = Optional.ofNullable(event.getSysUser()).map(SysUser::getId).orElse(null);
        String username =
                Optional.ofNullable(event.getSysUser()).map(SysUser::getUsername).orElse(null);
        String requestBody = this.convert(event.getParams());
        String responseBody = null;
        if (null != event.getThrowable()) {
            responseBody = event.getThrowable().getMessage();
        } else {
            responseBody = event.isFileDownload() ? "文件下载" : this.convert(event.getResult());
        }
        String paramMap = null == event.getParameterMap() || event.getParameterMap().isEmpty() ?
                null : convert(event.getParameterMap());
        HttpLog httpLog = new HttpLog(IdWorker.snowflakeStringId(), event.getUri(),
                event.getMethod(), userId, username, convert(event.getHeaderMap()), paramMap,
                requestBody, responseBody, null, LocalDateTime.now());
        jdbcHelper.insert(httpLog);
    }


    private String convert(Object obj) {
        if (null == obj) {
            return null;
        }
        // 配置 ObjectMapper，忽略 null 值
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        // 序列化时忽略MultipartFile
        // 创建模块并注册自定义序列化器
        SimpleModule module = new SimpleModule();
        module.addSerializer(MultipartFile.class, new MultipartFileSerializer());
        mapper.registerModule(module);
        try {
            if (Collection.class.isAssignableFrom(obj.getClass())) {
                Collection array = (Collection) obj;
                if (null != array && array.size() != 0) {
                    Object val = array.stream().findFirst().get();
                    if (null == val) {
                        return null;
                    }
                    return mapper.writeValueAsString(val);
                }
            }
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
        }
        return null;
    }

    @PostConstruct
    public void init() {
        eventPublisher.eventBus().register(this);
    }


    public static class MultipartFileSerializer extends JsonSerializer<MultipartFile> {

        @Override
        public void serialize(MultipartFile file, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
            // 不执行任何操作，直接忽略该字段
        }
    }
}
