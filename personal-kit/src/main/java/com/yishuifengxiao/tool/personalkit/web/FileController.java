package com.yishuifengxiao.tool.personalkit.web;


import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
import com.yishuifengxiao.tool.personalkit.domain.request.IdListReq;
import com.yishuifengxiao.tool.personalkit.service.FileService;
import com.yishuifengxiao.tool.personalkit.tool.ContextUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Api(value = "文件管理", tags = {"文件管理"})
@Controller
@Valid
@Validated
@Slf4j
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;


    @ApiImplicitParams({
            //
            @ApiImplicitParam(name = "file[]", value = "文件流对象,接收数组格式", required = true, dataType = "MultipartFile", allowMultiple = true),
            //
            @ApiImplicitParam(name = "folder", value = "文件夹目录", required = false),
            //
            @ApiImplicitParam(name = "mode", value = "模式（true:解析，false仅存储）", required = false)})
    @ApiOperation(value = "多文件上传", notes = "使用多文件方式上传")
    @PostMapping("/imports")
    @ResponseBody
    public void importFiles(HttpServletRequest request, @RequestParam(value = "file[]", required = true) MultipartFile[] files,
                            //
                            @RequestParam(value = "folder", required = false) String folder,
                            //
                            @RequestParam(value = "mode", required = false) UploadMode uploadMode) throws IOException {
        final SysUser sysUser = ContextUser.currentUser();
        fileService.uploads(request, sysUser, folder, uploadMode, files);
    }

    @ApiImplicitParams({
            //
            @ApiImplicitParam(name = "file", value = "文件流对象,接收数组格式", allowMultiple = true, dataTypeClass = MultipartFile.class, required = true, dataType = "MultipartFile"),
            //
            @ApiImplicitParam(name = "folder", value = "文件夹目录", required = true),

            //
            @ApiImplicitParam(name = "mode", value = "模式（true:解析，false仅存储）", required = false)})
    @ApiOperation(value = "文件上传", notes = "使用文件方式上传")
    @PostMapping("/import")
    @ResponseBody
    public Object importFile(HttpServletRequest request, @RequestParam(value = "file", required = true) MultipartFile file,
                             //
                             @RequestParam(value = "folder", required = false) String folder,
                             //
                             @RequestParam(value = "traceId", required = false) String traceId,
                             //
                             @RequestParam(value = "mode", required = false) UploadMode uploadMode) throws IOException {
        final SysUser sysUser = ContextUser.currentUser();
        final String upload = fileService.upload(request, sysUser, folder, uploadMode, file, traceId);
        return upload;
    }


    @ApiOperation(value = "批量删除文件", notes = "批量删除文件")
    @PostMapping("/deletes")
    @ResponseBody
    public void deletes(HttpServletRequest request, @RequestBody IdListReq req, BindingResult errors) {
        fileService.delete(req);
    }

    @ApiImplicitParams(@ApiImplicitParam(name = "id", value = "文件夹目录的id", required = true))
    @ApiOperation(value = "文件分享", notes = "文件分享")
    @GetMapping("/share")
    @ResponseBody
    public String share(HttpServletRequest request, @RequestParam String id) {
        final String share = fileService.share(id);
        return share;
    }

}
