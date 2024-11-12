package com.yishuifengxiao.tool.personalkit;

import com.yishuifengxiao.common.jdbc.JdbcHelper;
import com.yishuifengxiao.common.jdbc.util.ColumnNameRowMapper;
import com.yishuifengxiao.common.tool.bean.JsonUtil;
import com.yishuifengxiao.tool.personalkit.domain.entity.AutoTable;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUserRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

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
    @Autowired
    private JdbcHelper jdbcHelper;


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
        List<String> list = jdbcTemplate.query("select sp.url from sys_permission sp ;",
                new BeanPropertyRowMapper(String.class));
        System.out.println(JsonUtil.toJSONString(list));
    }

    @Test
    public void test_saveorUpdate() {
        SysUserRole userRole = new SysUserRole("10000", null, null);
        KeyHolder keyHolder = jdbcHelper.saveOrUpdate(userRole);
        String key = keyHolder.getKeyAs(String.class);
        Map<String, Object> keys = keyHolder.getKeys();
        System.out.println(key);
        System.out.println(keys);
        System.out.println("-------");
    }

    @Test
    public void test_saveorUpdate_autoTable() {
        AutoTable userRole = new AutoTable(null, "age", null);
        KeyHolder keyHolder = jdbcHelper.saveOrUpdate(userRole);
        Map<String, Object> keys = keyHolder.getKeys();
        System.out.println(keys);
        System.out.println("-------");
    }

    @Test
    public void test_saveorUpdate_autoTable1() {
        AutoTable userRole = new AutoTable(6L, "age", null);
        KeyHolder keyHolder = jdbcHelper.saveOrUpdate(userRole);
        Map<String, Object> keys = keyHolder.getKeys();
        System.out.println(keys);
        System.out.println("-------");
    }

    @Test
    public void test_findAll() {
        List<SysRole> sysRoles = jdbcHelper.findAll(new SysRole().setName("aaaaaaaaaaaaa"), false);
        System.out.println(sysRoles);
        SysRole one = jdbcHelper.findOne(new SysRole().setName("aaaaaaaaaaaaa"), false);
        System.out.println(one);
    }
}
