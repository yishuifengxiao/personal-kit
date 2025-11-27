package com.yishuifengxiao.tool.personalkit.service.gsma.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitiateAuthenticationReq implements Serializable {
    private String euiccChallenge;
    private String euiccInfo1;
    private String smdpAddress;
}
