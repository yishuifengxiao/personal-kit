package com.yishuifengxiao.tool.personalkit.domain.bo;

import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RequestLogEvent implements Serializable {
    private long key;
    private Long useMillis;
    private String uri;
    private String method;
    private String queryString;
    private Map<String, String> requestHeaderMap;
    private Map<String, String[]> parameterMap;
    private Map<String, String> responseHeaderMap;
    private SysUser sysUser;


}
