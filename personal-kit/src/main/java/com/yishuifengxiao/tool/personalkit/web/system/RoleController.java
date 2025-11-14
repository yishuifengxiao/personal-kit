package com.yishuifengxiao.tool.personalkit.web.system;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.validate.Group;
import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.query.RoleQuery;
import com.yishuifengxiao.tool.personalkit.domain.request.IdListReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.RoleVo;
import com.yishuifengxiao.tool.personalkit.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "角色管理")
@Valid
@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
@Trim
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/page")
    public Page<RoleVo> findPageRole(@RequestBody PageQuery<RoleQuery> pageQuery) {
        Page<RoleVo> page = roleService.findPageRole(pageQuery);

        return page;

    }

    @PostMapping("/add")
    public void addRole(@Validated(Group.Create.class) @RequestBody RoleVo param,
                        BindingResult errors) {
        roleService.addRole(param);
    }

    @PostMapping("/update")
    public void updateRole(@Validated(Group.Create.class) @RequestBody RoleVo param,
                           BindingResult errors) {
        roleService.updateRole(param);
    }

    @PostMapping("/deletes")
    public void deleteRole(@Valid @RequestBody IdListReq param) {
        roleService.deleteRoles(param.getIds());
    }


}
