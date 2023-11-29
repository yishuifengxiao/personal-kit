package com.yishuifengxiao.tool.personalkit.domain.model;

import com.yishuifengxiao.tool.personalkit.domain.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/29 23:14
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class VirtuallyRow implements Serializable {


    private String id;

    private String fileId;

    private String userId;

    private Long index;

    private Boolean isNormal;

    private List<ExcelCell> cells;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class ExcelCell implements Serializable {

        private String text;

        private BigDecimal decimal;

        private Long columnIndex;

        private Boolean isNormal;


        private DataType dataType;

    }


}
