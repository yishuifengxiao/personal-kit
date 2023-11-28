package com.yishuifengxiao.tool.personalkit.domain.vo;

import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/26 21:49
 * @since 1.0.0
 */
@Data
@Validated
public class UserVo extends SysUser implements Serializable {

    @Valid
    private List<SysRole> roles;
}
