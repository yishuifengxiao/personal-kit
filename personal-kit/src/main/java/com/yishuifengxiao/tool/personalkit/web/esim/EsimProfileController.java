package com.yishuifengxiao.tool.personalkit.web.esim;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.bo.Profile;
import com.yishuifengxiao.tool.personalkit.domain.entity.esim.EsimProfile;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.domain.request.IdsReq;
import com.yishuifengxiao.tool.personalkit.service.esim.EsimProfileService;
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
@RequestMapping("/api/esim/profile")
@RequiredArgsConstructor
@Trim
public class EsimProfileController {

    @Autowired
    private EsimProfileService esimProfileService;

    @PostMapping("/page")
    @ResponseBody
    public Page<EsimProfile> findPage(@RequestBody PageQuery<EsimProfile> pageQuery) {
        return esimProfileService.findPage(pageQuery);
    }

    @PostMapping(value = "/save", produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public EsimProfile save(@RequestBody Profile param) {
        return esimProfileService.save(param);
    }

    @PostMapping("/update")
    @ResponseBody
    public void update(@RequestBody Profile param) {
        esimProfileService.update(param);
    }

    @PostMapping("/deletes")
    @ResponseBody
    public void delete(@Valid @RequestBody IdsReq req) {
        esimProfileService.deleteByIds(req.getIds());
    }

    @PostMapping("/detail")
    @ResponseBody
    public Object detail(@Valid @RequestBody IdReq param) {
        return esimProfileService.findById(Long.valueOf(param.getId()));
    }
}
