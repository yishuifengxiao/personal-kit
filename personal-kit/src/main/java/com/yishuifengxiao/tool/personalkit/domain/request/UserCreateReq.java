package com.yishuifengxiao.tool.personalkit.domain.request;

import com.yishuifengxiao.common.tool.validate.Group;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Valid
@Data
public class UserCreateReq implements Serializable {
    @NotBlank(groups = {Group.Create.class}, message = "账号不能为空")
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String certNo;
    @NotNull(groups = {Group.Create.class}, message = "账号不能为空")
    @NotEmpty(groups = {Group.Create.class}, message = "账号不能为空")
    private List<String> roleIds;
}
