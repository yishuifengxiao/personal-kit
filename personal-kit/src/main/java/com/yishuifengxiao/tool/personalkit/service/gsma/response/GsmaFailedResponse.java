package com.yishuifengxiao.tool.personalkit.service.gsma.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GsmaFailedResponse implements Serializable {
    private Header header;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Header implements Serializable {

        private FunctionExecutionStatus functionExecutionStatus;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class FunctionExecutionStatus implements Serializable {
        private String status = "Failed";
        private StatusCodeData statusCodeData;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class StatusCodeData implements Serializable {

        private String subjectCode;

        private String reasonCode;

        private String message;
    }

    /**
     * 构建GsmaFailedResponse对象
     *
     * @param subjectCode 主题代码，用于标识错误的主题类别
     * @param reasonCode  原因代码，用于标识具体的错误原因
     * @param message     错误消息，提供详细的错误描述信息
     * @return 返回构建好的GsmaFailedResponse对象，包含完整的错误状态信息
     */
    public static GsmaFailedResponse build(String subjectCode, String reasonCode, String message) {
        // 创建GsmaFailedResponse对象，并设置包含错误状态信息的Header
        return new GsmaFailedResponse().setHeader(new Header(new FunctionExecutionStatus().setStatusCodeData(new StatusCodeData().setSubjectCode(subjectCode).setReasonCode(reasonCode).setMessage(message))));
    }

}
