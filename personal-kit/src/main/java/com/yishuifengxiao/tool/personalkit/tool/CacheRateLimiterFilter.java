package com.yishuifengxiao.tool.personalkit.tool;

import com.google.common.util.concurrent.RateLimiter;
import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.http.HttpUtil;
import com.yishuifengxiao.common.utils.HttpUtils;
import com.yishuifengxiao.tool.personalkit.config.CoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/12/21 15:15
 * @since 1.0.0
 */
@Component
public class CacheRateLimiterFilter extends OncePerRequestFilter {
    @Autowired
    private CoreProperties coreproperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (coreproperties.getIpMaxVisitPerSecond() > 0 && !request.getRequestURI().contains(".")) {
            //开启了限流功能
            String visitorIp = HttpUtil.getVisitorIp(request);
            RateLimiter rateLimiter = GuavaCache.get(visitorIp,
                    () -> RateLimiter.create(coreproperties.getIpMaxVisitPerSecond(), Duration.ofSeconds(1)));
            if (!rateLimiter.tryAcquire()) {
                // 触发了限流
                HttpUtils.write(request, response, Response.error("您的访问较为频繁，请稍后一段时间后再试"));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
