package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.entity.UploadRecord;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
import com.yishuifengxiao.tool.personalkit.service.sys.RecordService;
import com.yishuifengxiao.tool.personalkit.service.UploadService;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
@Validated
@Slf4j
@RequestMapping("/upload")
@Trim
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;
    private final RecordService recordService;

    @PostMapping("/file")
    @ResponseBody
    public Response upload(HttpServletRequest request,
                           @RequestParam(value = "file", required = true) MultipartFile file,
                           //
                           @RequestParam(value = "folder", required = false) String folder,
                           //
                           @RequestParam(value = "traceId", required = false) String traceId,
                           //
                           @RequestParam(value = "mode", required = false) UploadMode uploadMode) throws IOException {
        final SysUser sysUser = ContextCache.currentLoginUser();
        uploadMode = uploadMode == null ? UploadMode.UPLOAD : uploadMode;
        final String upload = uploadService.upload(request, sysUser, folder, uploadMode, file,
                traceId);
        return Response.suc(upload);
    }

    @PostMapping("/record")
    @ResponseBody
    public Page<UploadRecord> record(@RequestBody PageQuery<UploadRecord> pageQuery) {

        return recordService.findPageUploadRecord(pageQuery);
    }
}
