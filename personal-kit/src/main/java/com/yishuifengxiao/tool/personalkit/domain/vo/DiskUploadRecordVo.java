package com.yishuifengxiao.tool.personalkit.domain.vo;

import com.yishuifengxiao.tool.personalkit.domain.entity.DiskUploadRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 23:31
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DiskUploadRecordVo extends DiskUploadRecord implements Serializable {

    /**
     * 实际数据的数量
     */
    private Long actualTotalNum;

    /**
     * 全部数据数量
     */
    private Long uploadNum;

    private String statName;

    private String  userName;

    private List<FileItem> files;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class FileItem implements Serializable{
        /**
         * 实际数据的数量
         */
        private Long actualTotalNum;

        /**
         * 全部数据数量
         */
        private Long uploadNum;
        private String fileId;
        private String fileName;

        private String   VirtualFileId;
        private String   VirtualFileName;


    }
}
