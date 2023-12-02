package com.yishuifengxiao.tool.personalkit.domain.bo;

import com.yishuifengxiao.tool.personalkit.domain.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 18:16
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ExcelRow implements Serializable {

    protected Long rowIndex;

    protected List<ExcelCell> cells;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class ExcelCell implements Serializable {
        private Integer columnIndex;

        private String columnName;

        private String text;

        private BigDecimal decimal;

        private LocalDateTime time;


        private Boolean isNormal;


        private DataType dataType;

    }
}
