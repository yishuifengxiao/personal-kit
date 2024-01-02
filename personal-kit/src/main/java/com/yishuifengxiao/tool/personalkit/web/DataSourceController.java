package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskUploadRecord;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyRow;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.DiskUploadRecordVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.VirtuallyFileVo;
import com.yishuifengxiao.tool.personalkit.service.DataCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 13:01
 * @since 1.0.0
 */
@Api(value = "数据中心", tags = {"数据中心"})
@Valid
@Validated
@Slf4j
@RequestMapping("/data/center")
@RestController
public class DataSourceController {

    @Autowired
    private DataCenterService dataCenterService;

    @ApiOperation(value = "上传记录", notes = "上传记录")
    @PostMapping("/upload/page")
    public Page<DiskUploadRecordVo> reacordPage(@RequestBody PageQuery<DiskUploadRecord> pageQuery) {

        return dataCenterService.findPageDataRecord(pageQuery);
    }

    @ApiOperation(value = "查询虚拟文件定义", notes = "根据主键查询虚拟文件定义")
    @PostMapping("/struct")
    public List<VirtuallyFile.VirtuallyHeader> struct(@Valid @RequestBody IdReq req) {
        return dataCenterService.findVirtuallyFileDefine(req.getId());
    }

    @ApiOperation(value = "分页查询文件内容", notes = "分页查询虚拟文件的文件内容")
    @PostMapping("/contents")
    public Page<VirtuallyRow> contents(@Valid @RequestBody PageQuery<VirtuallyRow> pageQuery) {
        return dataCenterService.findPageVirtuallyRow(pageQuery);
    }

    @ApiOperation(value = "分页查询文件定义和内容", notes = "分页查询文件定义和内容")
    @PostMapping("/view")
    public VirtuallyFileVo view(@Valid @RequestBody PageQuery<VirtuallyRow> pageQuery) {
        return dataCenterService.findPageVirtuallyData(pageQuery);
    }
}
