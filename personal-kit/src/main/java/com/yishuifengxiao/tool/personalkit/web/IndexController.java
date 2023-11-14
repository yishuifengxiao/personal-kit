package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.tool.collections.MapUtil;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.tool.personalkit.domain.query.LoginQuery;
import com.yishuifengxiao.tool.personalkit.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Response<Authentication> user(Authentication authentication) {
        return Response.suc(authentication);
    }


    @ApiOperation("获取当前用户的token列表")
    @GetMapping("/tokens")
    @ResponseBody
    public Response<List<SecurityToken>> tokens(Authentication authentication) {
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


        return Response.sucData(tokens);
    }

    @ApiOperation(value = "index", notes = "index")
    @RequestMapping("/index")
    @ResponseBody
    public Response<Object> index(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

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

        Map map = MapUtil.map().put("requestURL", requestURL).put("requestURI", requestURI).put("contextPath", contextPath)
                .put("servletPath", servletPath).put("parameterMap", parameterMap).put("headerMap", headerMap).build();
        return Response.sucData(map);
    }

    @ApiOperation("登录接口")
    @PostMapping("/login")
    @org.springframework.web.bind.annotation.ResponseBody
    public Response login(HttpServletRequest request, @Valid @RequestBody LoginQuery query, BindingResult error) throws CustomException {

        return Response.suc(userService.login(request, query));
    }


}
