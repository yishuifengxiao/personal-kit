package com.yishuifengxiao.tool.personalkit.tool.impl;

import com.yishuifengxiao.tool.personalkit.config.CoreProperties;
import com.yishuifengxiao.tool.personalkit.tool.CacheRateLimiter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/3 21:20
 * @since 1.0.0
 */
@Component
public class SimpleCacheRateLimiter implements CacheRateLimiter {

    private final Map<String, Entry> map = new HashMap<>();

    @Autowired
    private CoreProperties coreProperties;


    @Override
    public synchronized void set(String key) {
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime expireAt = time.plus(coreProperties.getLimitTimeInSecond(), ChronoUnit.SECONDS);
        map.put(key, new Entry(time, expireAt));
    }

    @Override
    public synchronized boolean get(String key) {
        Entry entry = map.get(key);
        if (null == entry) {
            return false;
        }
        if (entry.expireAt.isBefore(LocalDateTime.now())) {
            map.put(key, null);
            return true;
        }
        return false;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Entry implements Serializable {

        private LocalDateTime createAt;

        private LocalDateTime expireAt;
    }
}
