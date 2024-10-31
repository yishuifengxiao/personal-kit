package com.yishuifengxiao.tool.personalkit.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import com.yishuifengxiao.common.guava.EventPublisher;
import com.yishuifengxiao.tool.personalkit.domain.bo.RequestLogEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class RequestLogEventListener {
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private ObjectMapper mapper;

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
                map.put(headerName, s.getHeader(headerName));
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
    }


    private String convert(Object obj) {
        if (null == obj) {
            return null;
        }
        try {
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
