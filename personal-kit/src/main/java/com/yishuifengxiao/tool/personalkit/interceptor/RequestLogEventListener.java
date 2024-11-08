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
import com.yishuifengxiao.common.tool.bean.JsonUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.tool.personalkit.domain.bo.RequestLogEvent;
import com.yishuifengxiao.tool.personalkit.domain.entity.HttpLog;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class RequestLogEventListener {

    private final static ObjectMapper mapper = JsonUtil.mapper();
    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private JdbcHelper jdbcHelper;


    @Subscribe
    public void onRequestLogEvent(RequestLogEvent event) {

        String userId = Optional.ofNullable(event.getSysUser()).map(SysUser::getId).orElse(null);
        String username =
                Optional.ofNullable(event.getSysUser()).map(SysUser::getUsername).orElse(null);
        Object requestCache = CacheUtils.getRequestCache(event.getKey());
        Object responseCache = CacheUtils.getResponseCache(event.getKey());
        LocalDateTime requestTime = CacheUtils.getRequestTime(event.getKey());
        String requestBody = this.convert(requestCache);
        String responseHeaderMap = this.convert(event.getResponseHeaderMap());
        String contentDisposition = event.getResponseHeaderMap().getOrDefault("Content"
                + "-Disposition", "");
        String responseBody = StringUtils.isNotBlank(contentDisposition) ? "文件下载" :
                this.convert(responseCache);
        String parameterMap = this.convert(event.getParameterMap());

        String requestHeaderMap = this.convert(event.getRequestHeaderMap());
        CacheUtils.clear(event.getKey());
        HttpLog httpLog = new HttpLog(IdWorker.snowflakeStringId(), event.getUri(),
                event.getMethod(), userId, username, requestHeaderMap, parameterMap,
                requestBody, responseBody, event.getUseMillis(), null, requestTime);
        jdbcHelper.insert(httpLog);
    }


    private String convert(Object obj) {
        if (null == obj) {
            return null;
        }
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public static class MultipartFileSerializer extends JsonSerializer<MultipartFile> {
        @Override
        public void serialize(MultipartFile value, JsonGenerator gen,
                              SerializerProvider serializers) throws IOException {
            // 什么也不做，直接忽略这个字段
        }
    }

    static {
        // 禁用写入类信息
        try {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
            // 忽略 MultipartFile
            SimpleModule module = new SimpleModule();
            // 注册 MultipartFile 的自定义序列化器
            module.addSerializer(MultipartFile.class, new MultipartFileSerializer());
            mapper.registerModule(module);
        } catch (Exception e) {
        }

    }

    @PostConstruct
    public void init() {
        eventPublisher.eventBus().register(this);
    }
}
