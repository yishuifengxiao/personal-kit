package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.mongo.DataSet;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.DataSetDetail;
import com.yishuifengxiao.tool.personalkit.domain.vo.DataSetVo;
import com.yishuifengxiao.tool.personalkit.service.DataSetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 18:43
 * @since 1.0.0
 */
@Tag(name = "数据集管理")
@Valid
@Validated
@Slf4j
@RequestMapping("/data/dataSet")
@RestController
public class DataSetController {

    @Autowired
    private DataSetService dataSetService;

    @Operation(summary = "分页查询数据集", description = "分页查询数据集描述")
    @PostMapping("/page")
    public Page<DataSetVo> reacordPage(@RequestBody PageQuery<DataSet> pageQuery) {

        return dataSetService.findPageDataSet(pageQuery);
    }

    @Operation(summary = "增加数据集", description = "增加数据集描述")
    @PostMapping("/save")
    public void add(@RequestBody DataSet param) {

        dataSetService.save(param);
    }

    @Operation(summary = "更新数据集", description = "更新数据集描述")
    @PostMapping("/update")
    public void update(@RequestBody DataSet param) {

        dataSetService.update(param);
    }

    @Operation(summary = "删除数据集", description = "删除数据集描述")
    @PostMapping("/delete")
    public void delete(@Valid @RequestBody IdReq param) {

        dataSetService.delete(param);
    }

    @Operation(summary = "数据集详情", description = "数据集详情描述")
    @PostMapping("/detail")
    public DataSetDetail detail(@Valid @RequestBody IdReq param) {

        return dataSetService.detail(param);
    }
}
