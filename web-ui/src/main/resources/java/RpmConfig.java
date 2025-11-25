package com.example.personal_kit;

import java.util.List;

public class RpmConfig {
    private List<String> rpmType;
    private String rpmDownloadMethod;
    private String rpmPollingAddress;
    private String allowedCA;
    private List<String> allowedTags;

    // Getters and Setters
    public List<String> getRpmType() {
        return rpmType;
    }

    public void setRpmType(List<String> rpmType) {
        this.rpmType = rpmType;
    }

    public String getRpmDownloadMethod() {
        return rpmDownloadMethod;
    }

    public void setRpmDownloadMethod(String rpmDownloadMethod) {
        this.rpmDownloadMethod = rpmDownloadMethod;
    }

    public String getRpmPollingAddress() {
        return rpmPollingAddress;
    }

    public void setRpmPollingAddress(String rpmPollingAddress) {
        this.rpmPollingAddress = rpmPollingAddress;
    }

    public String getAllowedCA() {
        return allowedCA;
    }

    public void setAllowedCA(String allowedCA) {
        this.allowedCA = allowedCA;
    }

    public List<String> getAllowedTags() {
        return allowedTags;
    }

    public void setAllowedTags(List<String> allowedTags) {
        this.allowedTags = allowedTags;
    }

    @Override
    public String toString() {
        return "RpmConfig{" +
                "rpmType=" + rpmType +
                ", rpmDownloadMethod='" + rpmDownloadMethod + '\'' +
                ", rpmPollingAddress='" + rpmPollingAddress + '\'' +
                ", allowedCA='" + allowedCA + '\'' +
                ", allowedTags=" + allowedTags +
                '}';
    }
}