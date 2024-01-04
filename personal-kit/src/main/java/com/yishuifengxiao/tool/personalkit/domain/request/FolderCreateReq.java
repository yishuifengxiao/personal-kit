package com.yishuifengxiao.tool.personalkit.domain.request;

import com.yishuifengxiao.common.tool.validate.Group;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Validated
@Valid
public class FolderCreateReq implements Serializable {

    /**
     * 数据唯一标识符
     */
    @NotBlank(message = "待操作的数据不能为空", groups = {Group.Update.class})
    private String id;

    /**
     * 文件夹的名字
     */
    @NotBlank(message = "文件夹的名字不能为空", groups = {Group.Create.class, Group.Update.class})
    private String name;

    @NotBlank(message = "父级文件夹id不能为空", groups = {Group.Create.class})
    private String parent;


}
