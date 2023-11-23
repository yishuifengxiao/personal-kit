package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.BaseQuery;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import com.yishuifengxiao.tool.personalkit.domain.query.RoleQuery;
import com.yishuifengxiao.tool.personalkit.domain.vo.PermissionVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.RoleVo;
import com.yishuifengxiao.tool.personalkit.service.SysService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
}
