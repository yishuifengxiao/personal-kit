package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.BaseQuery;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.validate.Group;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import com.yishuifengxiao.tool.personalkit.domain.query.RoleQuery;
import com.yishuifengxiao.tool.personalkit.domain.query.UserQuery;
import com.yishuifengxiao.tool.personalkit.domain.request.IdListReq;
import com.yishuifengxiao.tool.personalkit.domain.request.RoleUserReq;
import com.yishuifengxiao.tool.personalkit.domain.request.UserRoleReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.PermissionVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.RoleVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.UserVo;
import com.yishuifengxiao.tool.personalkit.service.SysService;
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
 * @date 2023/11/17 16:32
 * @since 1.0.0
 */
@Api(tags = {"系统接口"})
@Valid
@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class SystemController {

    private final SysService sysService;

    @ApiOperation(value = "分页查询资源")
    @PostMapping("/permission/page")
    @ResponseBody
    public Page<PermissionVo> findPagePermission(@RequestBody BaseQuery<SysPermission> pageQuery) {
        Page<PermissionVo> page = sysService.findPagePermission(pageQuery);

        return page;

    }

    @ApiOperation(value = "分页查询角色")
    @PostMapping("/role/page")
    @ResponseBody
    public Page<RoleVo> findPageRole(@RequestBody BaseQuery<RoleQuery> pageQuery) {
        Page<RoleVo> page = sysService.findPageRole(pageQuery);

        return page;

    }

    @ApiOperation(value = "分页查询用户")
    @PostMapping("/user/page")
    @ResponseBody
    public Page<UserVo> findPageUser(@RequestBody BaseQuery<UserQuery> pageQuery) {
        Page<UserVo> page = sysService.findPageUser(pageQuery);
        return page;
    }


    @ApiOperation(value = "添加一个角色")
    @PostMapping("/role/add")
    @ResponseBody
    public void addRole(@Validated(Group.Create.class) @RequestBody RoleVo param, BindingResult errors) {
        sysService.addRole(param);
    }

    @ApiOperation(value = "根据主键更新角色")
    @PostMapping("/role/update")
    @ResponseBody
    public void updateRole(@Validated(Group.Create.class) @RequestBody RoleVo param, BindingResult errors) {
        sysService.updateRole(param);
    }

    @ApiOperation(value = "根据主键更新角色")
    @PostMapping("/role/deletes")
    @ResponseBody
    public void deleteRole(@Valid @RequestBody IdListReq param) {
        sysService.deleteRoles(param.getIds());
    }

    @ApiOperation(value = "为角色分配用户")
    @PostMapping("/role/updateRoleUser")
    @ResponseBody
    public void updateRole(@Validated(Group.Update.class) @RequestBody RoleUserReq param, BindingResult errors) {
        sysService.updateRoleUser(param);
    }

    @ApiOperation(value = "为用户分配角色")
    @PostMapping("/role/UserRoleReq")
    @ResponseBody
    public void updateUserRole(@Validated(Group.Update.class) @RequestBody UserRoleReq param, BindingResult errors) {
        sysService.updateUserRole(param);
    }
}
