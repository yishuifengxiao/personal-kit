package com.yishuifengxiao.tool.personalkit.tool;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/3 21:18
 * @since 1.0.0
 */
public interface CacheRateLimiter {

    void set(String key);

    boolean get(String key);
}
