package com.yishuifengxiao.tool.personalkit.interceptor;

import com.yishuifengxiao.common.guava.GuavaCache;

import java.time.LocalDateTime;

public class CacheUtils {
    public final static String request_prefix = "request_prefix";
    public final static String response_prefix = "response_prefix";
    public final static String request_time_prefix = "request_time_prefix";

    public static void setRequestCache(Object data) {
        GuavaCache.put(request_prefix + Thread.currentThread().getId(), data);
    }

    public static void setResponseCache(Object data) {
        GuavaCache.put(response_prefix + Thread.currentThread().getId(), data);
    }

    public static Object getRequestCache() {
        return getRequestCache(Thread.currentThread().getId());
    }

    public static Object getResponseCache() {
        return getResponseCache(Thread.currentThread().getId());
    }

    public static Object getRequestCache(Object key) {
        return GuavaCache.get(request_prefix + key);
    }

    public static Object getResponseCache(Object key) {
        return GuavaCache.get(response_prefix + key);
    }


    public static void setRequestTime() {
        GuavaCache.put(request_time_prefix + Thread.currentThread().getId(), LocalDateTime.now());
    }

    public static LocalDateTime getRequestTime() {
        return GuavaCache.get(request_time_prefix + Thread.currentThread().getId(),
                () -> LocalDateTime.now());
    }

    public static LocalDateTime getRequestTime(Object key) {
        return GuavaCache.get(request_time_prefix + key,
                () -> LocalDateTime.now());
    }

    public static void clear() {
        GuavaCache.remove(request_prefix + Thread.currentThread().getId());
        GuavaCache.remove(response_prefix + Thread.currentThread().getId());
    }

    public static void clear(Object key) {
        GuavaCache.remove(request_prefix + key);
        GuavaCache.remove(response_prefix + key);
        GuavaCache.remove(request_time_prefix + key);
    }
}
