package com.yishuifengxiao.tool.personalkit.domain.request;

import com.yishuifengxiao.common.tool.validate.Group;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 22:20
 * @since 1.0.0
 */
@Data
@Validated
@Valid
public class FileMoveReq implements Serializable {

    /**
     * 数据唯一标识符
     */
    @NotBlank(message = "待操作的数据不能为空", groups = {Group.Update.class})
    private String id;

    /**
     * 数据唯一标识符
     */
    @NotBlank(message = "目标文件夹不能为空", groups = {Group.Update.class})
    private String folder;
}
