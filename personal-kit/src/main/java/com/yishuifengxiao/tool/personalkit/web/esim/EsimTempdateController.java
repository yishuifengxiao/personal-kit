package com.yishuifengxiao.tool.personalkit.web.esim;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.entity.esim.EsimTempdate;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.service.esim.EsimTempdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 19:02
 * @since 1.0.0
 */
@Valid
@RestController
@RequestMapping("/api/esim/tempdate")
@RequiredArgsConstructor
@Trim
public class EsimTempdateController {

    @Autowired
    private EsimTempdateService esimTempdateService;

    @PostMapping("/page")
    @ResponseBody
    public Page<Map> findPage(@RequestBody PageQuery<EsimTempdate> pageQuery) {
        return esimTempdateService.findPage(pageQuery);
    }

    @PostMapping(value = "/save", produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public EsimTempdate save(@RequestBody EsimTempdate param) {
        return esimTempdateService.save(param);
    }

    @PostMapping("/update")
    @ResponseBody
    public void update(@RequestBody EsimTempdate param) {
        esimTempdateService.update(param);
    }

    @PostMapping("/delete")
    @ResponseBody
    public void delete(@Valid @RequestBody IdReq param) {
        esimTempdateService.deleteById(Long.valueOf(param.getId()));
    }

    @PostMapping("/detail")
    @ResponseBody
    public Object detail(@Valid @RequestBody IdReq param) {
        return esimTempdateService.findById(Long.valueOf(param.getId()));
    }
}
