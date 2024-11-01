package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.query.LoginQuery;
import com.yishuifengxiao.tool.personalkit.domain.vo.UserInfo;
import com.yishuifengxiao.tool.personalkit.service.UserService;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
@Tag(name = "首页")
@Valid
@Controller
public class IndexController {
    @Autowired
    private TokenHolder tokenHolder;
    @Autowired
    private UserService userService;

    @GetMapping("/currentUser")
    @ResponseBody
    public Authentication currentUser(Authentication authentication) {
        return authentication;
    }

    @Operation(summary = "当前登录用户信息", description = "获取当前登录用户信息")
    @GetMapping("/user")
    @ResponseBody
    public SysUser user() {
        return ContextCache.currentUser().orElse(null);
    }

    @Operation(summary = "获取token", description = "获取当前用户所有的token信息")
    @GetMapping("/tokens")
    @ResponseBody
    public List<SecurityToken> tokens(Authentication authentication) {
        List<SecurityToken> tokens = tokenHolder.getAll(authentication.getName());

        tokens.stream().map(token -> {
            String username = token.getName();
            LocalDateTime issueAt = token.getIssueAt();
            LocalDateTime expireAt = token.getExpireAt();
            String tokenValue = token.getValue();
            boolean tokenActive = token.isActive();
            boolean tokenExpired = token.isExpired();
            boolean tokenAvailable = token.isAvailable();

            return null;
        }).collect(Collectors.toList());


        return tokens;
    }

    @Operation(summary = "默认接口", description = "获取请求信息")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @ResponseBody
    public Object index(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) {

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

        Map map = Map.of("requestURL", requestURL, "requestURI", requestURI, "contextPath",
                contextPath, "servletPath", servletPath, "parameterMap", parameterMap, "headerMap"
                , headerMap);
        return map;
    }


    @Operation(summary = "登录", description = "使用用户名和密码进行登录")
    @PostMapping("/login")
    @org.springframework.web.bind.annotation.ResponseBody
    public UserInfo login(HttpServletRequest request, HttpServletResponse response,
                          @Valid @RequestBody LoginQuery query, BindingResult error) throws CustomException {

        UserInfo userInfo = userService.login(request, response, query);


        return userInfo;
    }


}
