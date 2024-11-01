package com.yishuifengxiao.tool.personalkit.domain.bo;

import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RequestLogEvent implements Serializable {
    private String uri;
    private String method;
    private Map<String, String> headerMap;
    private Map<String, String[]> parameterMap;
    private List<Object> params;
    private SysUser sysUser;
    private boolean isFileUpload;
    private boolean isFileDownload;
    private Object result;
    private Throwable throwable;
}
