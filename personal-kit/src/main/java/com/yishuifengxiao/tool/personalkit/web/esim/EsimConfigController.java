package com.yishuifengxiao.tool.personalkit.web.esim;

import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.vo.EsimConfigResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
@Valid
@RestController
@RequestMapping("/api/esim/config")
@RequiredArgsConstructor
@Trim
public class EsimConfigController {


    @PostMapping("/all")
    @ResponseBody
    public EsimConfigResp config() {
        List<EsimConfigResp.NotifyAddr> notifyAddrs = new ArrayList<>();
        notifyAddrs.add(new EsimConfigResp.NotifyAddr("通知地址1", "http://www.baidu.com"));
        notifyAddrs.add(new EsimConfigResp.NotifyAddr("通知地址2", "http://www.baidu.com"));
        return new EsimConfigResp(notifyAddrs);
    }
}
