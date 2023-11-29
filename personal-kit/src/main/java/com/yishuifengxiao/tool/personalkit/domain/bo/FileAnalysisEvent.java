package com.yishuifengxiao.tool.personalkit.domain.bo;

import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFolder;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskUploadRecord;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/29 21:52
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FileAnalysisEvent implements Serializable {

    private DiskFolder diskFolder;

    private SysUser sysUser;

    private String filePath;

    private UploadMode uploadMode;
    private
    DiskUploadRecord uploadRecord;
}
