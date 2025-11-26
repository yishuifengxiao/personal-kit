package com.yishuifengxiao.tool.personalkit;

import com.yishuifengxiao.common.jdbc.JdbcHelper;
import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JdbcTest {

    @Autowired
    private JdbcHelper jdbcHelper;

    @Test
    public void testA1_in_query() {
        String sql = "SELECT * from sys_user where id in (:ids)";
        List<SysUser> all = jdbcHelper.findAll(SysUser.class, sql, Arrays.asList(1, 2));
        System.out.println(all);
    }
}
