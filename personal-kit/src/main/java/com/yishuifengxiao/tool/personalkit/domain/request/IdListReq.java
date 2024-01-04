package com.yishuifengxiao.tool.personalkit.domain.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/26 22:39
 * @since 1.0.0
 */
@Valid
@Data
public class IdListReq implements Serializable {

    @NotNull(message = "请至少选择一条记录")
    @NotEmpty(message = "请至少选择一条记录")
    private List<String> ids;
}
