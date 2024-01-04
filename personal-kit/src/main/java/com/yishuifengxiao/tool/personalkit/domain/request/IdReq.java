package com.yishuifengxiao.tool.personalkit.domain.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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
