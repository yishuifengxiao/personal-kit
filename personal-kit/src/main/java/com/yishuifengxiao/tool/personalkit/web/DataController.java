package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFile;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskUploadRecord;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyRow;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.DiskUploadRecordVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.VirtuallyFileVo;
import com.yishuifengxiao.tool.personalkit.service.DataCenterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 13:01
 * @since 1.0.0
 */
@Tag(name = "数据管理")
@Valid
@Validated
@Slf4j
@RequestMapping("/data/center")
@RestController
@Trim
public class DataController {

    @Autowired
    private DataCenterService dataCenterService;

    @PostMapping("/upload/page")
    public Page<DiskUploadRecordVo> reacordPage(@RequestBody PageQuery<DiskUploadRecord> pageQuery) {

        return dataCenterService.findPageDataRecord(pageQuery);
    }

    @PostMapping("/file/page")
    public Page<DiskFile> filesPage(@RequestBody PageQuery<DiskFile> pageQuery) {

        return dataCenterService.findPageDiskFile(pageQuery);
    }

    @PostMapping("/struct")
    public List<VirtuallyFile.VirtuallyHeader> struct(@Valid @RequestBody IdReq req) {
        return dataCenterService.findVirtuallyFileDefine(req.getId());
    }

    @PostMapping("/contents")
    public Page<VirtuallyRow> contents(@Valid @RequestBody PageQuery<VirtuallyRow> pageQuery) {
        return dataCenterService.findPageVirtuallyRow(pageQuery);
    }

    @PostMapping("/view")
    public VirtuallyFileVo view(@Valid @RequestBody PageQuery<VirtuallyRow> pageQuery) {
        return dataCenterService.findPageVirtuallyData(pageQuery);
    }
}
