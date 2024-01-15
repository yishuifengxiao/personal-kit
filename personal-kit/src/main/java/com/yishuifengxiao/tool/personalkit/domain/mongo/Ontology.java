package com.yishuifengxiao.tool.personalkit.domain.mongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yishuifengxiao.tool.personalkit.domain.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/12/21 19:15
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(Ontology.COLLECTION_NAME)
public class Ontology implements Serializable {
    public final static String COLLECTION_NAME = "ontology";
    @Id
    private String id;

    private String ontologyName;

    private String description;

    private String createUserId;

    private Integer version;

    private Boolean master;


    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    private List<Node> nodes;

    private List<Edge> edges;

    private String text;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Node implements Serializable {
        private String nodeName;

        private List<NodeProperty> nodeProperties;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class NodeProperty implements Serializable {
        private String nodePropertyName;

        private DataType dataType;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Edge implements Serializable {
        private String edgeName;

        private String formNodeName;

        private String formNodePropertyName;

        private String toNodeName;

        private String toNodePropertyName;

        private BigDecimal weight;

        public Edge(String edgeName, String formNodeName, String toNodeName) {
            this.edgeName = edgeName;
            this.formNodeName = formNodeName;
            this.toNodeName = toNodeName;
        }
    }
}
