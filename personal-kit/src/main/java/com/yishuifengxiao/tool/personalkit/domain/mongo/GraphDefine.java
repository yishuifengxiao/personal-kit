package com.yishuifengxiao.tool.personalkit.domain.mongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yishuifengxiao.tool.personalkit.domain.enums.ExtractRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/12/21 19:24
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(GraphDefine.COLLECTION_NAME)
public class GraphDefine implements Serializable {
    public final static String COLLECTION_NAME = "graph_define";
    @Id
    private String id;

    private String graphName;

    private String description;

    private String createUserId;

    private Integer version;

    private Boolean master;

    private String ontologyId;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    private List<DataSource> dataSources;

    private List<DataFusion> dataFusions;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class DataSource implements Serializable {
        private String virtuallyFileId;
        private List<NodeMapping> nodeMappings;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class NodeMapping implements Serializable {
        private String nodeName;

        private String nodePropertyName;

        private String rowName;

        private List<DataExtractRule> dataExtractRules;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class DataExtractRule implements Serializable {
        private ExtractRule extractrule;

        private String script;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class DataFusion implements Serializable {
        private String nodeName;

        private String script;

    }


}
