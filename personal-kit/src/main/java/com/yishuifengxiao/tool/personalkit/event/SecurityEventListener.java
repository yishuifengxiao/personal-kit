package com.yishuifengxiao.tool.personalkit.event;

import com.google.common.util.concurrent.RateLimiter;
import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.security.support.SecurityEvent;
import com.yishuifengxiao.common.security.support.Strategy;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.authentication.SimpleWebAuthenticationDetails;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.common.tool.utils.ExceptionUtil;
import com.yishuifengxiao.tool.personalkit.config.CoreProperties;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysSecurityRecord;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/17 11:39
 * @since 1.0.0
 */
@Component
public class SecurityEventListener {

    private final static Set<Strategy> STRATEGYS = DataUtil.asSet(Strategy.AUTHENTICATION_FAILURE, Strategy.ACCESS_DENIED, Strategy.ON_EXCEPTION);


    @Autowired
    private CoreProperties coreproperties;

    @Autowired
    private SysUserDao sysUserDao;


    @EventListener(SecurityEvent.class)
    public void onEvent(SecurityEvent event) {
        HttpServletRequest request = event.getRequest();
        Authentication authentication = event.getAuthentication();
        String name = null;
        String tokenVal = null;
        if (null != authentication) {
            name = authentication.getName();
            if (authentication instanceof SecurityToken) {
                SecurityToken securityToken = (SecurityToken) authentication;
                tokenVal = securityToken.getValue();
            } else {
                if (null != authentication.getDetails() && authentication.getDetails() instanceof SimpleWebAuthenticationDetails) {
                    SimpleWebAuthenticationDetails details = (SimpleWebAuthenticationDetails) authentication.getDetails();
                    if (null != details && null != details.getToken()) {
                        tokenVal = details.getToken().getValue();
                    }
                }

            }
        }

        requestRateLimiter(name, event.getStrategy(), request);

        //@formatter:off
        SysSecurityRecord sysSecurityRecord = new SysSecurityRecord().setId(IdWorker.snowflakeStringId())
                .setUsername(name).setToken(tokenVal)
                .setContentType(request.getContentType())
                .setUserAgent(request.getHeader("user-agent"))
                .setReferer(request.getHeader("referer"))
                .setIp(request.getRemoteAddr())
                .setAccept(request.getHeader("accept"))
                .setUrl(request.getRequestURL().toString())
                .setCookie(request.getHeader("cookie"))
                .setStrategy(event.getStrategy().getCode())
                .setMsg(Optional.ofNullable(event.getException()).map(Throwable::getMessage).orElse(null))
                .setException(ExceptionUtil.extractError( event.getException()))
                .setCreateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli( event.getTimestamp()),ZoneId.of("+8")));
        JdbcUtil.jdbcHelper().insertSelective(sysSecurityRecord);
    }
    private void requestRateLimiter(String name, Strategy strategy, HttpServletRequest request) {
        if (STRATEGYS.contains(strategy)&&StringUtils.isNotBlank(name)) {
            //认证失败、无权限、失败 ,防止暴力破解
            RateLimiter rateLimiter = GuavaCache.get(name, () -> RateLimiter.create(coreproperties.getPermitsPerSecond()));
            if(! rateLimiter.tryAcquire()){
                //触发限流
                SysUser activeSysUser = sysUserDao.findActiveSysUser(name).orElse(null);
                if(null!=activeSysUser){
                    sysUserDao.updateDisableTime(activeSysUser.getId(),LocalDateTime.now().plus(coreproperties.getLimitTimeInSecond(),
                            ChronoUnit.SECONDS));
                }

            }
        }

    }
}
