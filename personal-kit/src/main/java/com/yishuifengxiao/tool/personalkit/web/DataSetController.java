package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.DataSet;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.DataSetVo;
import com.yishuifengxiao.tool.personalkit.service.DataSetService;
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
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 18:43
 * @since 1.0.0
 */
@Api(value = "数据集", tags = {"数据集"})
@Valid
@Validated
@Slf4j
@RequestMapping("/data/dataSet")
@RestController
public class DataSetController {

    @Autowired
    private DataSetService dataSetService;

    @ApiOperation(value = "分页查询数据集", notes = "分页查询数据集")
    @PostMapping("/page")
    public Page<DataSetVo> reacordPage(@RequestBody PageQuery<DataSet> pageQuery) {

        return dataSetService.findPageDataSet(pageQuery);
    }

    @ApiOperation(value = "增加数据集", notes = "增加数据集")
    @PostMapping("/save")
    public void add(@RequestBody DataSet param) {

        dataSetService.save(param);
    }

    @ApiOperation(value = "更新数据集", notes = "更新数据集")
    @PostMapping("/update")
    public void update(@RequestBody DataSet param) {

        dataSetService.update(param);
    }

    @ApiOperation(value = "删除数据集", notes = "删除数据集")
    @PostMapping("/delete")
    public void delete(@Valid @RequestBody IdReq param) {

        dataSetService.delete(param);
    }


}
