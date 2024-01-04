package com.yishuifengxiao.tool.personalkit.domain.mongo;

import com.yishuifengxiao.tool.personalkit.domain.enums.DataType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/29 23:27
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(VirtuallyFile.COLLECTION_NAME)
public class VirtuallyFile implements Serializable {
    public final static String COLLECTION_NAME = "virtually_file";

    @Id
    private String id;

    private String fileId;

    private String userId;

    private String sheetName;

    private List<VirtuallyHeader> headers;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class VirtuallyHeader implements Serializable {

        private Integer columnIndex;

        private String name;

        private DataType dataType;
    }


}
