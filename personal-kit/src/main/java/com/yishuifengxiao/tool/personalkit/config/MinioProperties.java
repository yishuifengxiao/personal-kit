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
@ConfigurationProperties(prefix = "yishuifengxiao.minio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinioProperties {


    private String url;

    private String accessKey;

    private String secretKey;
}
