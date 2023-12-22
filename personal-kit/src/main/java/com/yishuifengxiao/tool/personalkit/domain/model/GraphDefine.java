package com.yishuifengxiao.tool.personalkit.domain.model;

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
public class GraphDefine implements Serializable {

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

    private List<DataSource> datasources;

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

    public enum ExtractRule {
        ANY("原始值", DataType.ANY), CONVERT_TEXT("转换为单值文本", DataType.TEXT), CONVERT_LONG("转换为单值整数", DataType.LONG), CONVERT_DOUBLE("转换为单值小数", DataType.DOUBLE), SPILT("切割", DataType.TEXT_ARRAY),

        REGEX("正则单值", DataType.TEXT),

        REGEX_ARRAY("正则数组", DataType.TEXT);


        private String name;
        private DataType resultType;

        ExtractRule(String name, DataType resultType) {
            this.name = name;
            this.resultType = resultType;
        }

        public String getName() {
            return name;
        }

        public DataType getResultType() {
            return resultType;
        }
    }


}