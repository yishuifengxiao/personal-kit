package com.yishuifengxiao.tool.personalkit.domain.mongo;

import com.yishuifengxiao.tool.personalkit.domain.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Id;
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
public class VirtuallyFile implements Serializable {


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
