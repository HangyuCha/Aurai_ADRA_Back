package com.metaverse.aurai_adra.dto;

public class AppProgressRequest {
    private String appId;
    private String type; // "practice" or "learn"
    private String at;   // ISO timestamp optional

    public AppProgressRequest() {}

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAt() { return at; }
    public void setAt(String at) { this.at = at; }
}