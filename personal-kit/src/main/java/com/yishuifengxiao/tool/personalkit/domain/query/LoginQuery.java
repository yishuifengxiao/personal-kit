package com.yishuifengxiao.tool.personalkit.domain.query;

import com.yishuifengxiao.common.tool.validate.Group;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/2-9:57
 * @since 1.0.0
 */
@Data
@Valid
public class LoginQuery implements Serializable {


    @NotBlank(message = "账号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;


}
