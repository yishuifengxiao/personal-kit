package com.yishuifengxiao.tool.personalkit.web.system;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysPermission;
import com.yishuifengxiao.tool.personalkit.domain.vo.PermissionVo;
import com.yishuifengxiao.tool.personalkit.service.PermissionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/17 16:32
 * @since 1.0.0
 */
@Tag(name = "权限管理")
@Valid
@RestController
@RequestMapping("/sys/permission")
@RequiredArgsConstructor
@Trim
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/page")
    public Page<PermissionVo> findPagePermission(@RequestBody PageQuery<SysPermission> pageQuery) {
        Page<PermissionVo> page = permissionService.findPagePermission(pageQuery);

        return page;

    }


}
