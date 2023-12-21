package com.yishuifengxiao.tool.personalkit.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/30 22:59
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "yishuifengxiao.personal")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoreProperties {


    /**
     * 默认为 允许的最大尝试次数为每分钟6次，即将速率值设置为每秒0.1=(6/60)次
     */

    private Double permitsPerSecond = 0.1D;


    /**
     * 禁止使用的时间，单位为秒，默认为24小时
     */
    private Long limitTimeInSecond = 60 * 60 * 24L;

    /**
     * 单个IP每秒中最大访问频率，默认为 允许的最大尝试次数为每分钟600次，即将速率值设置为每秒10=(600/60)次，若设置为0或负数则表示禁用此功能
     */

    private Double ipMaxVisitPerSecond = 10D;
}
