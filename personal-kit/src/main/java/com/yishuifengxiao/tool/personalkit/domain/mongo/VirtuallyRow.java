package com.yishuifengxiao.tool.personalkit.domain.mongo;

import com.yishuifengxiao.tool.personalkit.domain.bo.ExcelRow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
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
@Document(VirtuallyRow.COLLECTION_NAME)
public class VirtuallyRow extends ExcelRow implements Serializable {
    public final static String COLLECTION_NAME = "virtually_row";

    @Id
    private String id;

    private String fileId;

    private String userId;

    private String virtuallyFileId;

    private Boolean isNormal;

    public VirtuallyRow(Long rowIndex, List<ExcelCell> cells, String id, String fileId, String userId, String virtuallyFileId, Boolean isNormal) {
        super(rowIndex, cells);
        this.id = id;
        this.fileId = fileId;
        this.userId = userId;
        this.virtuallyFileId = virtuallyFileId;
        this.isNormal = isNormal;
    }
}
