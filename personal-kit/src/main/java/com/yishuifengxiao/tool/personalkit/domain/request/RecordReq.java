package com.yishuifengxiao.tool.personalkit.domain.request;

import com.yishuifengxiao.common.security.support.Strategy;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/3 22:53
 * @since 1.0.0
 */
@Data
public class RecordReq {

    private LocalDateTime startTime;

    private LocalDateTime endTime;


    /**
     * @see Strategy
     */
    private List<Integer> strategys;

    private String username;
}
