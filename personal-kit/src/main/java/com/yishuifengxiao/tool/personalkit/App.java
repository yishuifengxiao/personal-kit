package com.yishuifengxiao.tool.personalkit;

import com.yishuifengxiao.tool.personalkit.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Date;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/10/31-13:20
 * @since 1.0.0
 */
@Slf4j
@SpringBootApplication
public class App extends SpringBootServletInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {


        return builder.sources(App.class);

    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(App.class, args);

        runAfterStart(context);
        log.info("=============》 启动成功 {}", new Date());

    }

    private static void runAfterStart(ConfigurableApplicationContext context) {
        ConfigurableEnvironment env = context.getEnvironment();
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        StringBuilder url =
                new StringBuilder("http://localhost:").append(serverPort).append(StringUtils.isBlank(contextPath) ? "" : contextPath.trim()).append("/doc.html");
        log.info("=============》 在线文档地址 {}", url.toString());
        IpUtils.localIps().forEach(ip -> {
            log.info("=============》 在线文档地址 {}",
                    new StringBuilder("http://" + ip + ":").append(serverPort).append(StringUtils.isBlank(contextPath) ? "" : contextPath.trim()).append("/doc.html").toString());
        });
    }

}
