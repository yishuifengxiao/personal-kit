package com.yishuifengxiao.tool.personalkit.web.system;

import com.yishuifengxiao.common.security.support.Strategy;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.entity.HttpLog;
import com.yishuifengxiao.tool.personalkit.domain.request.HttpLogReq;
import com.yishuifengxiao.tool.personalkit.domain.request.RecordReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.SysSecurityRecordVo;
import com.yishuifengxiao.tool.personalkit.service.RecordService;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/3 18:35
 * @since 1.0.0
 */
@Tag(name = "记录管理")
@Valid
@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;


    @PostMapping("/personal/login/page")
    public Page<SysSecurityRecordVo> loginRecord(@RequestBody PageQuery<RecordReq> param) {

        RecordReq recordReq = param.query().orElse(new RecordReq());
        recordReq.setUsername(ContextCache.currentUser().get().getUsername());
        recordReq.setStrategys(Arrays.asList(Strategy.AUTHENTICATION_SUCCESS.code(),
                Strategy.AUTHENTICATION_FAILURE.code()));

        return recordService.findPageSysSecurityRecord(param);
    }

    @PostMapping("/personal/loginRecord/clear")
    public void clearLoginRecord() {


        recordService.clearLoginRecord();
    }

    @PostMapping("/personal/visitRecord/clear")
    public void clearVisitRecord() {


        recordService.clearVisitRecord();
    }


    @PostMapping("/personal/visit/page")
    public Page<HttpLog> visitRecord(@RequestBody PageQuery<HttpLogReq> param) {


        return recordService.findPageVisitRecord(param);
    }


}
