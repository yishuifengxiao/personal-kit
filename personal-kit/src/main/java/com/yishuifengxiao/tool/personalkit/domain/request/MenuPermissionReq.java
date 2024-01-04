package com.yishuifengxiao.tool.personalkit.domain.request;

import com.yishuifengxiao.common.tool.validate.Group;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/9 21:34
 * @since 1.0.0
 */
@Valid
@Data
public class MenuPermissionReq implements Serializable {

    @NotBlank(message = "请选择一条记录", groups = {Group.Update.class})
    private String id;

    @NotNull(message = "请至少选择一条权限记录", groups = {Group.Update.class})
    private List<String> permissionIds;
}
