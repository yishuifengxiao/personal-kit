package com.yishuifengxiao.tool.personalkit.event;

import com.fasterxml.jackson.annotation.JsonInclude;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        String requestBody = event.isFileUpload() ? "文件上传" : this.convert(event.getParams());
        String responseBody = event.isFileDownload() ? "文件下载" : this.convert(event.getResult());
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
}
