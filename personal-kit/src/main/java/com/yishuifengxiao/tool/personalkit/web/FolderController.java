package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.tool.personalkit.domain.request.FolderCreateReq;
import com.yishuifengxiao.tool.personalkit.domain.request.FolderNameReq;
import com.yishuifengxiao.tool.personalkit.domain.request.FolderParentReq;
import com.yishuifengxiao.tool.personalkit.domain.request.IdListReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.ResourceVo;
import com.yishuifengxiao.tool.personalkit.service.FolderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Api(value = "文件夹管理", tags = {"文件夹管理"})
@RestController
@Valid
@Validated
@Slf4j
@RequestMapping("/disk/folder")
public class FolderController {
    @Autowired
    private FolderService folderService;


    @ApiImplicitParams(@ApiImplicitParam(name = "folderId", value = "文件夹目录的id", required = false))
    @ApiOperation(value = "查询文件夹下面所有资源", notes = "若文件夹目录的id为空或不存在则表示查询根目录下所有的资源")
    @GetMapping("/list")
    public ResourceVo list(HttpServletRequest request,
                           @RequestParam(name = "folderId", required = false) String folderId) {
        final ResourceVo resourceVo = folderService.list(folderId);
        return resourceVo;
    }

    @ApiOperation(value = "创建文件夹", notes = "创建文件夹")
    @PostMapping("/create")
    public void create(HttpServletRequest request, @RequestBody FolderCreateReq req, BindingResult errors) {
        folderService.createFolder(req);
    }

    @ApiOperation(value = "修改文件夹名字", notes = "修改文件夹名字")
    @PostMapping("/update/name")
    public void updateFolderName(HttpServletRequest request, @RequestBody FolderNameReq req, BindingResult errors) {
        folderService.updateFolderName(req);
    }

    @ApiOperation(value = "移动文件夹位置", notes = "移动文件夹位置")
    @PostMapping("/update/parent")
    public void updateFolderParent(HttpServletRequest request, @RequestBody FolderParentReq req, BindingResult errors) {
        folderService.updateFolderParent(req);
    }

    @ApiOperation(value = "删除文件夹", notes = "删除文件夹")
    @PostMapping("/delete")
    public void folders(HttpServletRequest request, @RequestBody IdListReq req, BindingResult errors) {
        folderService.deleteById(req);
    }
}
