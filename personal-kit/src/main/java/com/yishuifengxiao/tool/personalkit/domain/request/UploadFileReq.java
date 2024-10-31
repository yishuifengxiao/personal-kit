package com.yishuifengxiao.tool.personalkit.domain.request;

import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2024/10/31 20:42
 * @since 1.0.0
 */
@Data
public class UploadFileReq {
    private MultipartFile file;
    //
    private String folder;
    //
    private String traceId;
    //
    private UploadMode uploadMode;
}
