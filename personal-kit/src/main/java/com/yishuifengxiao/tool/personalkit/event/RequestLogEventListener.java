package com.yishuifengxiao.tool.personalkit.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.eventbus.Subscribe;
import com.yishuifengxiao.common.guava.EventPublisher;
import com.yishuifengxiao.common.jdbc.JdbcHelper;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.tool.personalkit.domain.bo.RequestLogEvent;
import com.yishuifengxiao.tool.personalkit.domain.entity.HttpLog;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

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
        String method =
                Optional.ofNullable(event.getRequest()).filter(Objects::nonNull).map(s -> s.getMethod()).orElse(null);
        String uri =
                Optional.ofNullable(event.getRequest()).filter(Objects::nonNull).map(s -> s.getRequestURI()).orElse(null);
        String parameterMap =
                Optional.ofNullable(event.getRequest()).filter(Objects::nonNull).map(s -> s.getParameterMap()).map(this::convert).orElse(null);
        String headerMap =
                Optional.ofNullable(event.getRequest()).filter(Objects::nonNull).map(s -> {
            Map<String, String> map = new HashMap<>();
            s.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                String header = s.getHeader(headerName);
                if (!StringUtils.equalsAnyIgnoreCase(headerName, "cookie", "accept" + "-encoding"
                        , "connection", "accept-language", "user" + "-agent", "accept-language",
                        "content-length", "host", "origin") && StringUtils.isNotBlank(header)) {
                    map.put(headerName, header);
                }

            });
            return map;
        }).map(this::convert).orElse(null);
        // 获取请求的 Content-Type
        String contentType =
                Optional.ofNullable(event.getRequest()).map(s -> s.getContentType()).orElse(null);
        // 判断是否为文件上传
        boolean isFileUpload = contentType != null && contentType.startsWith("multipart/form-data");
        // 检查 Content-Disposition 头部
        String contentDisposition =
                Optional.ofNullable(event.getResponse()).map(s -> s.getHeader("Content" +
                        "-Disposition")).orElse(null);
        boolean isFileDownload = contentDisposition != null && contentDisposition.startsWith(
                "attachment");
        String userId = Optional.ofNullable(event.getSysUser()).map(SysUser::getId).orElse(null);
        String username =
                Optional.ofNullable(event.getSysUser()).map(SysUser::getUsername).orElse(null);
        String requestBody = isFileUpload ? "文件上传" : this.convert(event.getParams());
        String responseBody = isFileDownload ? "文件下载" : this.convert(event.getResult());
        HttpLog httpLog = new HttpLog(IdWorker.snowflakeStringId(), uri, method, userId, username
                , headerMap, parameterMap, requestBody, responseBody, null, LocalDateTime.now());
        jdbcHelper.insert(httpLog);
    }


    private String convert(Object obj) {
        if (null == obj) {
            return null;
        }
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        try {
            if (Collection.class.isAssignableFrom(obj.getClass())) {
                Collection array = (Collection) obj;
                if (null != array && array.size() != 0) {
                    Object val = array.stream().findFirst().get();
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
}
