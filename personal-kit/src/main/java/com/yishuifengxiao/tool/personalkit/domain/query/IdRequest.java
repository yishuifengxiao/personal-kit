package com.yishuifengxiao.tool.personalkit.domain.query;

import com.yishuifengxiao.common.tool.validate.Group;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/10-17:02
 * @since 1.0.0
 */
@Valid
public class IdRequest implements Serializable {

    @NotBlank(message = "请选择一条正确的记录", groups = {Group.Update.class})
    protected String id;
}
