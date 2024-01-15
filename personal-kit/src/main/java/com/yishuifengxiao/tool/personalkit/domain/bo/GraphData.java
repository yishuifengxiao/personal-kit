package com.yishuifengxiao.tool.personalkit.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2024/1/10 23:09
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GraphData implements Serializable {

    public final static String NODE_PROPERTIES = "nodeProperties";
    private String id;
    private String rootId;

    private String graphName;

    private String description;

    private List<Node> nodes;

    private List<Line> lines;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Node implements Serializable {
        private String id;

        private String text;

        private String nodeShape;

        private Boolean isHide;

        private String color;

        private Map<String, Object> data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Line implements Serializable {
        private String id;

        private String text;

        private String from;

        private String to;

        private String lineShape;

        private Boolean showEndArrow;

        private String color;


        private Map<String, Object> data;
    }

}
