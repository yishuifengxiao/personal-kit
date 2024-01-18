package com.yishuifengxiao.tool.personalkit.support;

import com.google.common.util.concurrent.RateLimiter;
import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.http.HttpUtil;
import com.yishuifengxiao.common.utils.HttpUtils;
import com.yishuifengxiao.tool.personalkit.config.CoreProperties;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

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

    private final Set<String> excludes = DataUtil.asSet("api-docs", "swagger", ".css", ".js", ".html");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            ContextUser.setRole(currentRole(request));
            if (coreproperties.getIpMaxVisitPerSecond() > 0 &&
                    //
                    !request.getRequestURI().contains(".") &&
                    //
                    excludes.stream().noneMatch(v -> StringUtils.containsIgnoreCase(request.getRequestURI(), v))
                //
            ) {
                //开启了限流功能WE
                String visitorIp = HttpUtil.getVisitorIp(request);
                RateLimiter rateLimiter = GuavaCache.get(visitorIp,
                        () -> RateLimiter.create(coreproperties.getIpMaxVisitPerSecond()));
                if (!rateLimiter.tryAcquire()) {
                    // 触发了限流
                    HttpUtils.write(request, response, Response.error("您的访问较为频繁，请稍后一段时间后再试"));
                    return;
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            ContextUser.clearRole();
        }

    }


    private String currentRole(HttpServletRequest request) {
        String currentRole = request.getHeader(Constant.CURRENT_ROLE);
        if (StringUtils.isNotBlank(currentRole)) {
            return currentRole.trim();
        }
        return request.getParameter(Constant.CURRENT_ROLE);
    }
}
