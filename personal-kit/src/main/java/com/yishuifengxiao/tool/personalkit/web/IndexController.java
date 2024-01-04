package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityEvent;
import com.yishuifengxiao.common.security.support.Strategy;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.support.SpringContext;
import com.yishuifengxiao.common.tool.collections.MapUtil;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.tool.personalkit.domain.query.LoginQuery;
import com.yishuifengxiao.tool.personalkit.domain.vo.LoginVo;
import com.yishuifengxiao.tool.personalkit.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/10/31-14:29
 * @since 1.0.0
 */
@Valid
@Controller
public class IndexController {
    @Autowired
    private TokenHolder tokenHolder;
    @Autowired
    private UserService userService;

    @GetMapping("/currentUser")
    @ResponseBody
    public Authentication user(Authentication authentication) {
        return authentication;
    }


    @GetMapping("/tokens")
    @ResponseBody
    public List<SecurityToken> tokens(Authentication authentication) {
        List<SecurityToken> tokens = tokenHolder.getAll(authentication.getName());

        tokens.stream().map(token -> {
            String username = token.getName();
            LocalDateTime issueAt = token.getIssueAt();
            LocalDateTime expireAt = token.getExpireAt();
            Integer validSeconds = token.getValidSeconds();
            String tokenValue = token.getValue();
            boolean tokenActive = token.isActive();
            boolean tokenExpired = token.isExpired();
            boolean tokenAvailable = token.isAvailable();

            return null;
        }).collect(Collectors.toList());


        return tokens;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @ResponseBody
    public Object index(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        StringBuffer requestURL = request.getRequestURL();
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String element = headerNames.nextElement();
            String header = request.getHeader(element);
            headerMap.put(element, header);
        }

        Map map = MapUtil.map().put("requestURL", requestURL).put("requestURI", requestURI).put("contextPath", contextPath).put("servletPath", servletPath).put("parameterMap", parameterMap).put("headerMap", headerMap).build();
        return map;
    }

    @Autowired
    private PropertyResource propertyResource;

    @PostMapping("/login")
    @org.springframework.web.bind.annotation.ResponseBody
    public LoginVo login(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody LoginQuery query, BindingResult error) throws CustomException {

        try {
            LoginVo loginVo = userService.login(request, response, query);
            SpringContext.publishEvent(new SecurityEvent(this, request, response, propertyResource, Strategy.AUTHENTICATION_SUCCESS, loginVo.getSecurityToken(), null));

            return loginVo;
        } catch (Exception e) {
            SpringContext.publishEvent(new SecurityEvent(this, request, response, propertyResource,
                    Strategy.AUTHENTICATION_SUCCESS, new SecurityToken(Collections.EMPTY_LIST) {
                @Override
                public String getName() {
                    return query.getUsername();
                }
            }, e));
            throw e;
        }
    }


}
