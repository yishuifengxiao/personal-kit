package com.yishuifengxiao.tool.personalkit.domain.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/3 22:37
 * @since 1.0.0
 */
@Valid
@Data
public class ResetPwdReq implements Serializable {

    @NotBlank(message = "账号不能为空")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    private String email;
}
