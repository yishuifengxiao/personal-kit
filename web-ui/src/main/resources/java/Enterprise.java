package com.example.personal_kit;

import java.util.List;

public class Enterprise {
    private String enterpriseName;
    private List<String> enterpriseRules;
    private int nonEnterpriseProfileCount;

    // Getters and Setters
    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public List<String> getEnterpriseRules() {
        return enterpriseRules;
    }

    public void setEnterpriseRules(List<String> enterpriseRules) {
        this.enterpriseRules = enterpriseRules;
    }

    public int getNonEnterpriseProfileCount() {
        return nonEnterpriseProfileCount;
    }

    public void setNonEnterpriseProfileCount(int nonEnterpriseProfileCount) {
        this.nonEnterpriseProfileCount = nonEnterpriseProfileCount;
    }

    @Override
    public String toString() {
        return "Enterprise{" +
                "enterpriseName='" + enterpriseName + '\'' +
                ", enterpriseRules=" + enterpriseRules +
                ", nonEnterpriseProfileCount=" + nonEnterpriseProfileCount +
                '}';
    }
}