package com.yishuifengxiao.tool.personalkit.domain.bo;

import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RequestLogEvent implements Serializable {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private SysUser sysUser;
    private List<Object> params;
    private Object result;
    private Throwable e;
}
