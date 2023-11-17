package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.tool.personalkit.domain.vo.UserInfo;
import com.yishuifengxiao.tool.personalkit.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/10-17:00
 * @since 1.0.0
 */
@Api(tags = {"用户接口"})
@Valid
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation("用户基本信息")
    @GetMapping("/info/{id}")
    public UserInfo info(@PathVariable String id) {
        return userService.userInfo(id);
    }
}
