package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.security.user.CurrentUser;
import com.yishuifengxiao.common.tool.validate.Group;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.request.ResetPwdReq;
import com.yishuifengxiao.tool.personalkit.domain.request.UpdatePwdReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.UserInfo;
import com.yishuifengxiao.tool.personalkit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/10-17:00
 * @since 1.0.0
 */
@Tag(name = "用户管理")
@Valid
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户基本信息", description = "获取指定用户的基本信息")
    @Parameters({
            @Parameter(name = "id", description = "用户Id", in = ParameterIn.PATH)
    })
    @GetMapping("/info/{id}")
    public UserInfo info(@PathVariable String id, @CurrentUser UserDetails user) {
        return userService.userInfo(id);
    }

    @PostMapping("/pwd/update")
    public void updatePwd(@Validated(Group.Update.class) @RequestBody UpdatePwdReq req,
                          BindingResult errors) {
        userService.updatePwd(req);
    }

    @PostMapping("/pwd/reset")
    public void resetPwd(@Valid @RequestBody ResetPwdReq req) {
        userService.updateResetPwd(req);
    }


    @PostMapping("/info/update")
    public void updateUser(@Validated(Group.Update.class) @RequestBody SysUser req,
                           BindingResult errors) {
        userService.updateUser(req);
    }


}
