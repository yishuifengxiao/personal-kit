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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/10/31-14:29
 * @since 1.0.0
 */
@Api(tags = {"首页接口"})
@Valid
@Controller
public class IndexController {
    @Autowired
    private TokenHolder tokenHolder;
    @Autowired
    private UserService userService;

    @ApiOperation(value = "当前用户信息-接口总结", notes = "当前用户信息-接口描述")
    @GetMapping("/currentUser")
    @ResponseBody
    public Authentication user(@ApiIgnore  Authentication authentication) {
        return authentication;
    }


    @ApiOperation("获取当前用户的token列表")
    @GetMapping("/tokens")
    @ResponseBody
    public List<SecurityToken> tokens(@ApiIgnore Authentication authentication) {
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

    @ApiOperation(value = "index", notes = "index")
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    @ResponseBody
    public Object index(HttpServletRequest request, HttpServletResponse response, @ApiIgnore Authentication authentication) {

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

    @ApiOperation("登录接口")
    @PostMapping("/login")
    @org.springframework.web.bind.annotation.ResponseBody
    public LoginVo login(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody LoginQuery query, BindingResult error) throws CustomException {

        try {
            LoginVo loginVo = userService.login(request,response, query);
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