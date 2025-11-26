package com.yishuifengxiao.tool.personalkit.listener.event;

import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFile;
import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.entity.UploadRecord;
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

    private DiskFile diskFile;
    private SysUser sysUser;
    private UploadMode uploadMode;
    private UploadRecord uploadRecord;
}
