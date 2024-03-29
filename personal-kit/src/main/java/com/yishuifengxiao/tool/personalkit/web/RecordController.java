package com.yishuifengxiao.tool.personalkit.web;

import com.yishuifengxiao.common.security.support.Strategy;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysSecurityRecord;
import com.yishuifengxiao.tool.personalkit.domain.request.RecordReq;
import com.yishuifengxiao.tool.personalkit.service.RecordService;
import com.yishuifengxiao.tool.personalkit.support.ContextUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/3 18:35
 * @since 1.0.0
 */
@Valid
@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;


    @PostMapping("/personal/login/page")
    public Page<SysSecurityRecord> loginRecord(PageQuery<RecordReq> param) {

        RecordReq recordReq = param.query().orElse(new RecordReq());
        recordReq.setUsername(ContextUser.currentUser().getUsername());
        recordReq.setStrategys(Arrays.asList(Strategy.AUTHENTICATION_SUCCESS.getCode(),
                Strategy.AUTHENTICATION_FAILURE.getCode()));

        return recordService.findPageSysSecurityRecord(param);
    }


}
