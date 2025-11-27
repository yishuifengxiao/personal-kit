package com.yishuifengxiao.tool.personalkit.web.gsma;

import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.web.SkipResponseWrapper;
import com.yishuifengxiao.tool.personalkit.service.gsma.GsmaException;
import com.yishuifengxiao.tool.personalkit.service.gsma.InitiateAuthentication;
import com.yishuifengxiao.tool.personalkit.service.gsma.request.InitiateAuthenticationReq;
import com.yishuifengxiao.tool.personalkit.service.gsma.response.GsmaFailedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestMapping("/gsma/rsp2/es9plus")
@RestController
@SkipResponseWrapper
@Slf4j
public class InitiateAuthenticationController {

    @Autowired
    private InitiateAuthentication initiateAuthentication;

    @PostMapping("/initiateAuthentication ")
    @ResponseBody
    public Object execute(@RequestHeader("X-Admin-Protocol") String protocol, @RequestBody InitiateAuthenticationReq req) {
        try {
            Object result = initiateAuthentication.execute(req);
            Map map = BeanUtil.beanToMap(result);
            map.put("protocol", protocol);
            Map<String, String> functionExecutionStatus = Map.of("status", "Executed-Success");
            Map<String, Object> header = Map.of("functionExecutionStatus", functionExecutionStatus);
            map.put("header", header);
            return map;
        } catch (GsmaException e) {
            return GsmaFailedResponse.build(e.getSubjectCode(), e.getReasonCode(), e.getMessage());
        } catch (Exception e) {
            log.warn("GSMA发起认证失败", e);
            return GsmaFailedResponse.build("500", "500", "GSMA发起认证失败");
        }

    }
}
