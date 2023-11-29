package com.yishuifengxiao.tool.personalkit.domain.model;

import com.yishuifengxiao.tool.personalkit.domain.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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

    private String id;

    private String fileId;

    private String userId;

    private List<VirtuallyHeader> headers;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class VirtuallyHeader implements Serializable {
        private String name;

        private Long columnIndex;

        private DataType dataType;
    }


}
