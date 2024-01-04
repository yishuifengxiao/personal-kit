package com.yishuifengxiao.tool.personalkit.domain.request;

import com.yishuifengxiao.common.tool.validate.Group;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/3 17:35
 * @since 1.0.0
 */
@Valid
@Data
public class UpdatePwdReq implements Serializable {

    @NotBlank(message = "用户id", groups = {Group.Update.class})
    private String id;

    @NotBlank(message = "旧密码不能为空", groups = {Group.Update.class})

    private String oldPwd;

    @NotBlank(message = "新密码不能为空", groups = {Group.Update.class})
    @Size(message = "密码长度在6到20位之间", min = 6, max = 20, groups = {Group.Update.class})
    private String newPwd;
}
