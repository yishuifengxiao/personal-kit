package com.yishuifengxiao.tool.personalkit.web.esim;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.entity.esim.EsimBusinessRecord;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.service.esim.EsimBusinessRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 19:02
 * @since 1.0.0
 */
@Valid
@RestController
@RequestMapping("/api/esim/record")
@RequiredArgsConstructor
@Trim
public class EsimBusinessRecordController {

    @Autowired
    private EsimBusinessRecordService esimBusinessRecordService;

    @PostMapping("/page")
    @ResponseBody
    public Page<EsimBusinessRecord> findPage(@RequestBody PageQuery<EsimBusinessRecord> pageQuery) {
        return esimBusinessRecordService.findPage(pageQuery);
    }

    @PostMapping(value = "/save", produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public EsimBusinessRecord save(@RequestBody EsimBusinessRecord param) {
        return esimBusinessRecordService.save(param);
    }

    @PostMapping("/update")
    @ResponseBody
    public void update(@RequestBody EsimBusinessRecord param) {
        esimBusinessRecordService.update(param);
    }

    @PostMapping("/delete")
    @ResponseBody
    public void delete(@Valid @RequestBody IdReq param) {
        esimBusinessRecordService.deleteById(Long.parseLong(param.getId()));
    }

    @PostMapping("/detail")
    @ResponseBody
    public Object detail(@Valid @RequestBody IdReq param) {
        return esimBusinessRecordService.findById(param.getId());
    }
}
