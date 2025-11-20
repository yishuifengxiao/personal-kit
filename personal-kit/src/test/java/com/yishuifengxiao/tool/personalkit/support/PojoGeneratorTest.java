package com.yishuifengxiao.tool.personalkit.support;

import com.yishuifengxiao.common.support.PojoGenerator;
import org.junit.Test;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
public class PojoGeneratorTest {

    @Test
    public void test() {
        // 创建配置
        PojoGenerator.GeneratorConfig config = new PojoGenerator.GeneratorConfig(
                "jdbc:mysql://10.28.1.219:3306/personkit",
                "root",
                "Whty2023sys"
        );

        // 配置参数
        config.setPackageName("com.yishuifengxiao.demo.entity");
        config.setOutputPath("src/test/java/com/yishuifengxiao/demo/entity");

        // 设置要去除的表前缀
        config.addTablePrefix("sys_");
        config.addTablePrefix("tbl_");
        config.addTablePrefix("app_");

        // 排除系统表
        config.addExcludeTable("schema_migrations");
        config.addExcludeTable("flyway_schema_history");

//        config.addIncludeTable("user_token");

        // 启用所有高级功能
        config.setUseLombok(true);
        config.setUseJpa(true);
        config.setUseSwagger(false);
        config.setUseJava8Date(true);
        config.setGenerateColumnDefinition(true);

        // 创建生成器并执行
        PojoGenerator generator = new PojoGenerator();
        generator.generateAllPojos(config);
    }
}
