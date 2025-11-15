package com.yishuifengxiao.tool.personalkit.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatReq implements Serializable {
    private String id;
    private Integer stat;
}
