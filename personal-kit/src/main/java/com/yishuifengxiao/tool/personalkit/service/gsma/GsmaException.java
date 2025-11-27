package com.yishuifengxiao.tool.personalkit.service.gsma;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class GsmaException extends RuntimeException {

    private String subjectCode;

    private String reasonCode;

    public GsmaException(String message, String subjectCode, String reasonCode) {
        super(message);
        this.subjectCode = subjectCode;
        this.reasonCode = reasonCode;
    }

    public GsmaException(String message, Throwable cause, String subjectCode, String reasonCode) {
        super(message, cause);
        this.subjectCode = subjectCode;
        this.reasonCode = reasonCode;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    /**
     * 抛出带有指定错误信息、主体代码和原因代码的GsmaException异常
     *
     * @param message     异常信息，描述异常的具体原因
     * @param subjectCode 主体代码，标识异常的主体类别
     * @param reasonCode  原因代码，标识异常的具体原因类型
     * @throws GsmaException 总是抛出GsmaException异常
     */
    public static void throwable(String message, String subjectCode, String reasonCode) {
        throw new GsmaException(message, subjectCode, reasonCode);
    }

    /**
     * 抛出带有指定错误信息、原因异常、主体代码和原因代码的GsmaException异常
     *
     * @param message     异常信息，描述异常的具体原因
     * @param cause       导致此异常的原始异常
     * @param subjectCode 主体代码，标识异常的主体类别
     * @param reasonCode  原因代码，标识异常的具体原因类型
     * @throws GsmaException 总是抛出GsmaException异常
     */
    public static void throwable(String message, Throwable cause, String subjectCode, String reasonCode) {
        throw new GsmaException(message, cause, subjectCode, reasonCode);
    }
}
