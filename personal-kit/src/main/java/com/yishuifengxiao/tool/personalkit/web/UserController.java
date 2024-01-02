package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.validate.Group;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.request.ResetPwdReq;
import com.yishuifengxiao.tool.personalkit.domain.request.UpdatePwdReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.UserInfo;
import com.yishuifengxiao.tool.personalkit.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation("用户基本信息")
    @GetMapping("/info/{id}")
    public UserInfo info(@PathVariable String id) {
        return userService.userInfo(id);
    }

    @ApiOperation("修改密码")
    @PostMapping("/pwd/update")
    public void updatePwd(@Validated(Group.Update.class) @RequestBody UpdatePwdReq req, BindingResult errors) {
        userService.updatePwd(req);
    }

    @ApiOperation("重置密码")
    @PostMapping("/pwd/reset")
    public void resetPwd(@Valid @RequestBody ResetPwdReq req) {
        userService.updateResetPwd(req);
    }


    @ApiOperation("修改基本信息")
    @PostMapping("/info/update")
    public void updateUser(@Validated(Group.Update.class) @RequestBody SysUser req, BindingResult errors) {
        userService.updateUser(req);
    }


}
