package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.security.user.CurrentUser;
import com.yishuifengxiao.common.tool.validate.Group;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.request.ResetPwdReq;
import com.yishuifengxiao.tool.personalkit.domain.request.UpdatePwdReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.UserInfo;
import com.yishuifengxiao.tool.personalkit.service.UserService;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
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

    @GetMapping("/info/{id}")
    public UserInfo info(@PathVariable String id, @CurrentUser UserDetails user) {
        SysUser sysUser = ContextCache.currentLoginUser();
        return userService.userInfo(id);
    }

    @PostMapping("/pwd/update")
    public void updatePwd(@Validated(Group.Update.class) @RequestBody UpdatePwdReq req, BindingResult errors) {
        userService.updatePwd(req);
    }

    @PostMapping("/pwd/reset")
    public void resetPwd(@Valid @RequestBody ResetPwdReq req) {
        userService.updateResetPwd(req);
    }


    @PostMapping("/info/update")
    public void updateUser(@Validated(Group.Update.class) @RequestBody SysUser req, BindingResult errors) {
        userService.updateUser(req);
    }


}
