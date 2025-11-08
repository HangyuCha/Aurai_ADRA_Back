package com.metaverse.aurai_adra.dto;

import java.time.Instant;
import java.util.Map;

public class AppSessionProgressRequest {
    private String appId;
    private String sessionId;
    private String type; // 'learn' or 'practice'
    private String at; // ISO timestamp optional
    private Map<String, Object> metadata;

    public AppSessionProgressRequest() {}

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAt() { return at; }
    public void setAt(String at) { this.at = at; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}