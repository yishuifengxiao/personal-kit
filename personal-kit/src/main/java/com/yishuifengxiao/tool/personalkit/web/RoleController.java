package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.validate.Group;
import com.yishuifengxiao.tool.personalkit.domain.query.RoleQuery;
import com.yishuifengxiao.tool.personalkit.domain.query.UserQuery;
import com.yishuifengxiao.tool.personalkit.domain.request.IdListReq;
import com.yishuifengxiao.tool.personalkit.domain.request.RoleUserReq;
import com.yishuifengxiao.tool.personalkit.domain.request.UserRoleReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.RoleVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.UserVo;
import com.yishuifengxiao.tool.personalkit.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/9 20:08
 * @since 1.0.0
 */
@Valid
@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/page")
    public Page<RoleVo> findPageRole(@RequestBody PageQuery<RoleQuery> pageQuery) {
        Page<RoleVo> page = roleService.findPageRole(pageQuery);

        return page;

    }

    @PostMapping("/user/page")
    public Page<UserVo> findPageUser(@RequestBody PageQuery<UserQuery> pageQuery) {
        Page<UserVo> page = roleService.findPageUser(pageQuery);
        return page;
    }


    @PostMapping("/add")
    public void addRole(@Validated(Group.Create.class) @RequestBody RoleVo param, BindingResult errors) {
        roleService.addRole(param);
    }

    @PostMapping("/update")
    public void updateRole(@Validated(Group.Create.class) @RequestBody RoleVo param, BindingResult errors) {
        roleService.updateRole(param);
    }

    @PostMapping("/deletes")
    public void deleteRole(@Valid @RequestBody IdListReq param) {
        roleService.deleteRoles(param.getIds());
    }

    @PostMapping("/updateRoleUser")
    public void updateRole(@Validated(Group.Update.class) @RequestBody RoleUserReq param, BindingResult errors) {
        roleService.updateRoleUser(param);
    }

    @PostMapping("/UserRoleReq")
    public void updateUserRole(@Validated(Group.Update.class) @RequestBody UserRoleReq param, BindingResult errors) {
        roleService.updateUserRole(param);
    }
}
