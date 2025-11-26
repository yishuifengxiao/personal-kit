package com.yishuifengxiao.tool.personalkit.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class EsimConfigResp implements Serializable {


    private List<NotifyAddr> notifyAddrs;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)

    public static class NotifyAddr implements Serializable {
        private String name;
        private String url;
    }
}
