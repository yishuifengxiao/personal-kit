package com.yishuifengxiao.tool.personalkit.web.esim;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.entity.EsimCert;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.service.EsimCertService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Esim证书管理")
@Valid
@RestController
@RequestMapping("/api/esim/cert")
@RequiredArgsConstructor
@Trim
public class EsimCertController {

    @Autowired
    private EsimCertService esimCertService;

    @PostMapping("/page")
    @ResponseBody
    public Page<EsimCert> findPage(@RequestBody PageQuery<EsimCert> pageQuery) {
        return esimCertService.findPage(pageQuery);
    }

    @PostMapping(value = "/save", produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public EsimCert save(@RequestBody EsimCert param) {
        return esimCertService.save(param);
    }

    @PostMapping("/update")
    @ResponseBody
    public void update(@RequestBody EsimCert param) {
        esimCertService.update(param);
    }

    @PostMapping("/delete")
    @ResponseBody
    public void delete(@Valid @RequestBody IdReq param) {
        esimCertService.deleteById(Long.parseLong(param.getId()));
    }

    @PostMapping("/detail")
    @ResponseBody
    public Object detail(@Valid @RequestBody IdReq param) {
        return esimCertService.findById(Long.parseLong(param.getId()));
    }
}
