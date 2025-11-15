package com.yishuifengxiao.tool.personalkit.web;


import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.request.FileMoveReq;
import com.yishuifengxiao.tool.personalkit.domain.request.IdListReq;
import com.yishuifengxiao.tool.personalkit.service.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Tag(name = "文件管理")
@Controller
@Valid
@Validated
@Slf4j
@RequestMapping("/disk/file")
@Trim
@RequiredArgsConstructor
public class FileController {

    @Autowired
    private FileService fileService;


    @PostMapping("/deletes")
    @ResponseBody
    public void deletes(HttpServletRequest request, @RequestBody IdListReq req,
                        BindingResult errors) {
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
    public void move(HttpServletRequest request, @RequestBody FileMoveReq req,
                     BindingResult errors) {
        fileService.move(req);
    }
}
