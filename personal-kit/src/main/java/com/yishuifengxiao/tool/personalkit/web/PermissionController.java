package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import com.yishuifengxiao.tool.personalkit.domain.vo.PermissionVo;
import com.yishuifengxiao.tool.personalkit.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/17 16:32
 * @since 1.0.0
 */
@Api(tags = {"资源接口"})
@Valid
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @ApiOperation(value = "分页查询资源")
    @PostMapping("/page")
    public Page<PermissionVo> findPagePermission(@RequestBody PageQuery<SysPermission> pageQuery) {
        Page<PermissionVo> page = permissionService.findPagePermission(pageQuery);

        return page;

    }


}
