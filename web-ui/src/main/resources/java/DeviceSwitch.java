package com.example.personal_kit;

public class DeviceSwitch {
    private String deviceSwitchMethod;
    private String needNewEID;
    private String needNewTAC;
    private String allowedCA;

    // Getters and Setters
    public String getDeviceSwitchMethod() {
        return deviceSwitchMethod;
    }

    public void setDeviceSwitchMethod(String deviceSwitchMethod) {
        this.deviceSwitchMethod = deviceSwitchMethod;
    }

    public String getNeedNewEID() {
        return needNewEID;
    }

    public void setNeedNewEID(String needNewEID) {
        this.needNewEID = needNewEID;
    }

    public String getNeedNewTAC() {
        return needNewTAC;
    }

    public void setNeedNewTAC(String needNewTAC) {
        this.needNewTAC = needNewTAC;
    }

    public String getAllowedCA() {
        return allowedCA;
    }

    public void setAllowedCA(String allowedCA) {
        this.allowedCA = allowedCA;
    }

    @Override
    public String toString() {
        return "DeviceSwitch{" +
                "deviceSwitchMethod='" + deviceSwitchMethod + '\'' +
                ", needNewEID='" + needNewEID + '\'' +
                ", needNewTAC='" + needNewTAC + '\'' +
                ", allowedCA='" + allowedCA + '\'' +
                '}';
    }
}