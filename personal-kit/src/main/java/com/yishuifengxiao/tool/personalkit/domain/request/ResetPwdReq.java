package com.yishuifengxiao.tool.personalkit.domain.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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
