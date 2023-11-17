package com.yishuifengxiao.tool.personalkit;

import com.yishuifengxiao.common.tool.collections.JsonUtil;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/10-15:42
 * @since 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class JdbcTemplateTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    public void test1() {
        List list = jdbcTemplate.query("select * from sys_role;", new ColumnNameRowMapper(SysRole.class));
        list.stream().map(JsonUtil::toJSONString).forEach(System.out::println);

    }

    @Test
    public void queryForListTest() {
        List<String> list = jdbcTemplate.queryForList("select sp.url from sys_permission sp ;", String.class);
        System.out.println(JsonUtil.toJSONString(list));
    }

    @Test
    public void queryForListTest1() {
        List<String> list = jdbcTemplate.query("select sp.url from sys_permission sp ;", new BeanPropertyRowMapper(String.class));
        System.out.println(JsonUtil.toJSONString(list));
    }
}
