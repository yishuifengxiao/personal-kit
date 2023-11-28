package com.yishuifengxiao.tool.personalkit.domain.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
