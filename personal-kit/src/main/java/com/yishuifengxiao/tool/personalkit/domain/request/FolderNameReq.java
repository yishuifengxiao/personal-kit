package com.yishuifengxiao.tool.personalkit.domain.request;

import com.yishuifengxiao.common.tool.validate.Group;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@ApiModel(value = "文件夹更新操作参数")
@Data
@Validated
@Valid
public class FolderNameReq implements Serializable {

    /**
     * 数据唯一标识符
     */
    @ApiModelProperty("数据唯一标识符,不能为空")
    @NotBlank(message = "待操作的数据不能为空", groups = {Group.Update.class})
    private String id;

    /**
     * 文件夹的名字
     */
    @ApiModelProperty("文件夹的名字,不能为空")
    @NotBlank(message = "文件夹的名字不能为空", groups = {Group.Create.class, Group.Update.class})
    private String name;


}
