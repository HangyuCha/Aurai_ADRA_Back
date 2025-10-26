package com.metaverse.aurai_adra.dto;

public class LearningAgeResponse {
    private String userId;
    private int decade;       // 80,70,...,10
    private String label;     // "80대"
    private int percent;      // 진행 퍼센트(0~100)
    private int successCount;
    private int totalChapters;

    public LearningAgeResponse(String userId, int decade, String label, int percent, int successCount, int totalChapters) {
        this.userId = userId;
        this.decade = decade;
        this.label = label;
        this.percent = percent;
        this.successCount = successCount;
        this.totalChapters = totalChapters;
    }

    public String getUserId() { return userId; }
    public int getDecade() { return decade; }
    public String getLabel() { return label; }
    public int getPercent() { return percent; }
    public int getSuccessCount() { return successCount; }
    public int getTotalChapters() { return totalChapters; }
}