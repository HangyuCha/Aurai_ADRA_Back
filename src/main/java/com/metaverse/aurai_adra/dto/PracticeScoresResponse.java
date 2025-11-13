package com.metaverse.aurai_adra.dto;

import java.util.List;

public class PracticeScoresResponse {
    private String appId; // sms|call|gpt|kakao (또는 null)
    private List<PracticeScoreItem> items;

    public PracticeScoresResponse() {}

    public PracticeScoresResponse(String appId, List<PracticeScoreItem> items) {
        this.appId = appId;
        this.items = items;
    }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public List<PracticeScoreItem> getItems() { return items; }
    public void setItems(List<PracticeScoreItem> items) { this.items = items; }
}