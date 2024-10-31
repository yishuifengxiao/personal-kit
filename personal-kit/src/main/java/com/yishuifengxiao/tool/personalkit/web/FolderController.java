package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.tool.personalkit.domain.request.FolderCreateReq;
import com.yishuifengxiao.tool.personalkit.domain.request.FolderNameReq;
import com.yishuifengxiao.tool.personalkit.domain.request.FolderParentReq;
import com.yishuifengxiao.tool.personalkit.domain.request.IdListReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.ResourceVo;
import com.yishuifengxiao.tool.personalkit.service.FolderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Tag(name = "文件夹管理")
@RestController
@Valid
@Validated
@Slf4j
@RequestMapping("/disk/folder")
public class FolderController {
    @Autowired
    private FolderService folderService;


    @GetMapping("/list")
    public ResourceVo list(HttpServletRequest request,
                           @RequestParam(name = "folderId", required = false) String folderId) {
        final ResourceVo resourceVo = folderService.list(folderId);
        return resourceVo;
    }

    @PostMapping("/create")
    public void create(HttpServletRequest request, @RequestBody FolderCreateReq req, BindingResult errors) {
        folderService.createFolder(req);
    }

    @PostMapping("/update/name")
    public void updateFolderName(HttpServletRequest request, @RequestBody FolderNameReq req, BindingResult errors) {
        folderService.updateFolderName(req);
    }

    @PostMapping("/update/parent")
    public void updateFolderParent(HttpServletRequest request, @RequestBody FolderParentReq req, BindingResult errors) {
        folderService.updateFolderParent(req);
    }

    @PostMapping("/delete")
    public void folders(HttpServletRequest request, @RequestBody IdListReq req, BindingResult errors) {
        folderService.deleteById(req);
    }
}
