package com.yishuifengxiao.tool.personalkit.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Profile主类
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private String id;
    private String iccid;
    private String matchingId;
    private String profileState;

    private String eid;
    private String localProfileState;
    private String downloadMethod;
    private String tenant;
    private String carrier;
    private String profileClass;
    private String pprPolicy;
    private String resetRule;
    private String dsFlag;
    private String remark;
    private String confirmationCode;
    private String profileName;
    private String phoneNumber;
    private String serviceProvider;
    private List<String> notificationEvent;
    private String notificationAddress;
    private String profileIcon;
    private String imsi;
    private String imsi2;
    private String pin1;
    private String pin2;
    private String puk1;
    private String puk2;
    private String adm1;
    private String ki;
    private String opc;
    private String smsp;
    private String asnTemplate;
    private Integer confirmLimit;
    private V3Support v3Support;

    /**
     * V3支持配置内部类
     */
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class V3Support {
        private List<String> features;
        private RpmConfig rpmConfig;
        private DeviceSwitch deviceSwitch;
        private Enterprise enterprise;


    }

    /**
     * RPM配置内部类
     */
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RpmConfig {
        private List<String> rpmType;
        private String rpmDownloadMethod;
        private String rpmPollingAddress;
        private String allowedCA;
        private List<String> allowedTags;
    }

    /**
     * 设备切换配置内部类
     */
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceSwitch {
        private String deviceSwitchMethod;
        private String needNewEID;
        private String needNewTAC;
        private String allowedCA;
    }

    /**
     * 企业配置内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Enterprise {
        private String enterpriseName;
        private List<String> enterpriseRules;
        private Integer nonEnterpriseProfileCount;
    }
}