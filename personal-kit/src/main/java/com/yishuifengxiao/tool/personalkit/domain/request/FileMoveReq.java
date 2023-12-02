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
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 22:20
 * @since 1.0.0
 */
@ApiModel(value = "文件移动操作参数")
@Data
@Validated
@Valid
public class FileMoveReq implements Serializable {

    /**
     * 数据唯一标识符
     */
    @ApiModelProperty("数据唯一标识符,不能为空")
    @NotBlank(message = "待操作的数据不能为空", groups = {Group.Update.class})
    private String id;

    /**
     * 数据唯一标识符
     */
    @ApiModelProperty("目标文件夹,不能为空")
    @NotBlank(message = "目标文件夹不能为空", groups = {Group.Update.class})
    private String folder;
}
