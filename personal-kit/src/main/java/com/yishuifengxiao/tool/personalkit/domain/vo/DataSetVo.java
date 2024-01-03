package com.yishuifengxiao.tool.personalkit.domain.vo;

import com.yishuifengxiao.tool.personalkit.domain.mongo.DataSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2024/1/3 21:20
 * @since 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataSetVo extends DataSet {

    private List<Item> sources;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Item implements Serializable {

        private String id;

        private String name;
    }
}
