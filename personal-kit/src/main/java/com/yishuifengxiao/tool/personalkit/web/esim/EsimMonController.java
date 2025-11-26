package com.yishuifengxiao.tool.personalkit.web.esim;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.entity.esim.EsimMon;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.service.esim.EsimMonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 19:02
 * @since 1.0.0
 */
@Tag(name = "Esim监控管理")
@Valid
@RestController
@RequestMapping("/api/esim/mon")
@RequiredArgsConstructor
@Trim
public class EsimMonController {

    @Autowired
    private EsimMonService esimMonService;

    @PostMapping("/page")
    @ResponseBody
    public Page<EsimMon> findPage(@RequestBody PageQuery<EsimMon> pageQuery) {
        return esimMonService.findPage(pageQuery);
    }

    @PostMapping("/list")
    @ResponseBody
    public List<EsimMon> list(@RequestBody EsimMon pageQuery) {
        PageQuery<EsimMon> query = PageQuery.of(pageQuery);
        return esimMonService.findPage(query).getData();
    }

    @PostMapping(value = "/save", produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public EsimMon save(@RequestBody EsimMon param) {
        return esimMonService.save(param);
    }

    @PostMapping("/update")
    @ResponseBody
    public void update(@RequestBody EsimMon param) {
        esimMonService.update(param);
    }

    @PostMapping("/delete")
    @ResponseBody
    public void delete(@Valid @RequestBody IdReq param) {
        esimMonService.deleteById(Long.valueOf(param.getId()));
    }

    @PostMapping("/detail")
    @ResponseBody
    public Object detail(@Valid @RequestBody IdReq param) {
        return esimMonService.findById(Long.valueOf(param.getId()));
    }
}
