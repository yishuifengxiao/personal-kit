package com.example.personal_kit;

import java.util.List;

public class V3Support {
    private List<String> features;
    private RpmConfig rpmConfig;
    private DeviceSwitch deviceSwitch;
    private Enterprise enterprise;

    // Getters and Setters
    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public RpmConfig getRpmConfig() {
        return rpmConfig;
    }

    public void setRpmConfig(RpmConfig rpmConfig) {
        this.rpmConfig = rpmConfig;
    }

    public DeviceSwitch getDeviceSwitch() {
        return deviceSwitch;
    }

    public void setDeviceSwitch(DeviceSwitch deviceSwitch) {
        this.deviceSwitch = deviceSwitch;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    @Override
    public String toString() {
        return "V3Support{" +
                "features=" + features +
                ", rpmConfig=" + rpmConfig +
                ", deviceSwitch=" + deviceSwitch +
                ", enterprise=" + enterprise +
                '}';
    }
}