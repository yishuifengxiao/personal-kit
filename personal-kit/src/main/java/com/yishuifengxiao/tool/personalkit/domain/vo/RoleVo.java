package com.yishuifengxiao.tool.personalkit.domain.vo;

import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysMenu;
import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysRole;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.List;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/20 16:25
 * @since 1.0.0
 */
@Data
@Validated
public class RoleVo extends SysRole implements Serializable {

    @Valid
    private List<SysMenu> menus;
}
