package com.yishuifengxiao.tool.personalkit;

import com.yishuifengxiao.common.tool.random.IdWorker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/22 17:20
 * @since 1.0.0
 */
@SpringBootTest(classes = App.class)
@RunWith(SpringRunner.class)
@Slf4j
public class DataTest {


    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    public void test() {
        List<Integer> list = Arrays.asList(1000);
        list.forEach(length -> {
            insertIdInc(length);
        });
        System.out.println("-------> 运行完成");
    }


    public void insertIdInc(int length) {
        jdbcTemplate.execute("truncate table id_inc ");
        StopWatch stopWatch = new StopWatch("【自增雪花ID】插入性能测试：数量-" + length);
        stopWatch.start("【自增雪花ID】[准备数据]：数量-" + length);
        List<IdIncrement> list = IntStream.range(0, length).mapToObj(v -> new IdIncrement(IdWorker.snowflakeId(),
                System.currentTimeMillis() + "", System.currentTimeMillis() + "", LocalDateTime.now())).collect(Collectors.toList());
        Long id = list.get(list.size() - 1).getId();
        stopWatch.stop();
        stopWatch.start("【自增雪花ID】[数据插入]：数量-" + length);
        String sql = "INSERT INTO `id_inc` ( `id`,`name`, `create_time`, `pwd`) VALUES ( ?,?, " + "?, ?) ";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setObject(1, list.get(i).getId());
                ps.setObject(2, list.get(i).getName());
                ps.setObject(3, list.get(i).getCreateTime());
                ps.setObject(4, list.get(i).getPwd());


                if (Objects.equals(i, list.size())) {
                    ps.executeLargeBatch();
                    ps.clearBatch();
                } else {
                    if (i % 500 == 0) {
                        ps.executeLargeBatch();
                        ps.clearBatch();
                    }
                }

            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
        stopWatch.stop();
        stopWatch.start("【自增雪花ID】[id查询]：数量-" + length);
        jdbcTemplate.queryForList("select * from id_inc where id = " + id);
        stopWatch.stop();
        stopWatch.start("【自增雪花ID】[查询10条]：数量-" + length);
        jdbcTemplate.queryForList("select * from id_inc limit 10");
        stopWatch.stop();
        stopWatch.start("【自增雪花ID】[id删除]：数量-" + length);
        jdbcTemplate.execute("delete  from id_inc where id = " + id);
        stopWatch.stop();
        stopWatch.start("【自增雪花ID】[随机删除]：数量-" + length);
        jdbcTemplate.execute("delete  from id_inc where id = " + System.currentTimeMillis());
        stopWatch.stop();
        log.warn("【自增雪花ID】耗时统计信息为\r\n {}", stopWatch.prettyPrint(TimeUnit.MILLISECONDS));

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IdIncrement {

        private Long id;

        private String name;
        private String pwd;

        private LocalDateTime createTime;
    }

}
