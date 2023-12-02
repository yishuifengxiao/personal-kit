package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.BaseQuery;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskUploadRecord;
import com.yishuifengxiao.tool.personalkit.domain.vo.DiskUploadRecordVo;
import com.yishuifengxiao.tool.personalkit.service.DataService;
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
@RequestMapping("/data")
@RestController
public class DataController {

    @Autowired
    private DataService dataService;

    @ApiOperation(value = "上传记录", notes = "上传记录")
    @PostMapping("/data/record/page")
    public Page<DiskUploadRecordVo> reacordPage(@RequestBody BaseQuery<DiskUploadRecord> pageQuery) {

        return dataService.findPageDataRecord(pageQuery);
    }
}
