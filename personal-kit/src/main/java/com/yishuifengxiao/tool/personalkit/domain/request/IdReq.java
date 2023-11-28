package com.yishuifengxiao.tool.personalkit.domain.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/26 22:40
 * @since 1.0.0
 */
@Valid
@Data
public class IdReq implements Serializable {

    @NotBlank(message = "请选择一个记录")
    private String id;
}
