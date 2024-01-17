package com.yishuifengxiao.tool.personalkit.web;


import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
import com.yishuifengxiao.tool.personalkit.domain.request.FileMoveReq;
import com.yishuifengxiao.tool.personalkit.domain.request.IdListReq;
import com.yishuifengxiao.tool.personalkit.service.FileService;
import com.yishuifengxiao.tool.personalkit.support.ContextUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
@Valid
@Validated
@Slf4j
@RequestMapping("/disk/file")
public class FileController {

    @Autowired
    private FileService fileService;



    @PostMapping("/imports")
    @ResponseBody
    public void importFiles(HttpServletRequest request,
                            @RequestParam(value = "file[]", required = true) MultipartFile[] files,
                            //
                            @RequestParam(value = "folder", required = false) String folder,
                            //
                            @RequestParam(value = "mode", required = false) UploadMode uploadMode) throws IOException {
        final SysUser sysUser = ContextUser.currentUser();
        fileService.uploads(request, sysUser, folder, uploadMode, files);
    }


    @PostMapping("/import")
    @ResponseBody
    public Response importFile(HttpServletRequest request,
                               @RequestParam(value = "file", required = true) MultipartFile file,
                               //
                               @RequestParam(value = "folder", required = false) String folder,
                               //
                               @RequestParam(value = "traceId", required = false) String traceId,
                               //
                               @RequestParam(value = "mode", required = false) UploadMode uploadMode) throws IOException {
        final SysUser sysUser = ContextUser.currentUser();
        final String upload = fileService.upload(request, sysUser, folder, uploadMode, file, traceId);
        return Response.sucData(upload);
    }


    @PostMapping("/deletes")
    @ResponseBody
    public void deletes(HttpServletRequest request, @RequestBody IdListReq req, BindingResult errors) {
        fileService.delete(req);
    }


    @GetMapping("/share")
    @ResponseBody
    public String share(HttpServletRequest request, @RequestParam String id) {
        final String share = fileService.share(id);
        return share;
    }


    @PostMapping("/move")
    @ResponseBody
    public void move(HttpServletRequest request, @RequestBody FileMoveReq req, BindingResult errors) {
        fileService.move(req);
    }
}
