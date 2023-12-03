package com.yishuifengxiao.tool.personalkit.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/30 23:00
 * @since 1.0.0
 */
@EnableConfigurationProperties({MinioProperties.class, CoreProperties.class})
@Configuration
public class CoreConfig {
}
