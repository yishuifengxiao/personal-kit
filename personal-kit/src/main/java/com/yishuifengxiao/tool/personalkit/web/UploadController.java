package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
import com.yishuifengxiao.tool.personalkit.service.UploadService;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
@RequestMapping("/file")
@Trim
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;

    @PostMapping("/upload")
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
        final String upload = uploadService.upload(request, sysUser, folder, uploadMode, file,
                traceId);
        return Response.suc(upload);
    }
}
