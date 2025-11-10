package com.yishuifengxiao.tool.personalkit.web.system;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.validate.Group;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.query.UserQuery;
import com.yishuifengxiao.tool.personalkit.domain.request.ResetPwdReq;
import com.yishuifengxiao.tool.personalkit.domain.request.UpdatePwdReq;
import com.yishuifengxiao.tool.personalkit.domain.request.UserCreateReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.PageUser;
import com.yishuifengxiao.tool.personalkit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
@Validated
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
    public SysUser info(@PathVariable String id,
                        @com.yishuifengxiao.common.security.user.CurrentUser UserDetails user) {
        return userService.userInfo(id);
    }

    @Operation(summary = "更新密码", description = "根据主键更新密码")
    @PostMapping("/pwd/update")
    public void updatePwd(HttpServletRequest request,
                          @Validated(Group.Update.class) @RequestBody UpdatePwdReq req) {
        userService.updatePwd(request, req);
    }

    @Operation(summary = "重置密码", description = "根据主键重置密码")
    @PostMapping("/pwd/reset")
    public void resetPwd(HttpServletRequest request, @Valid @RequestBody ResetPwdReq req) {
        userService.updateResetPwd(request, req);
    }

    @Operation(summary = "更新基本信息", description = "根据主键更新基本信息")
    @PostMapping("/info/update")
    public void updateUser(@Validated(Group.Update.class) @RequestBody SysUser req,
                           BindingResult errors) {
        userService.updateUser(req);
    }

    @Operation(summary = "分页查询", description = "分页查询用户基本信息")
    @PostMapping("/findPage")
    public Page<PageUser> findPage(@RequestBody PageQuery<UserQuery> query) {
        return userService.findPage(query);
    }

    @Operation(summary = "创建用户", description = "创建用户")
    @PostMapping("/create")
    public void create(@Validated(Group.Create.class) @RequestBody UserCreateReq user) {
        userService.create(user);
    }


}
