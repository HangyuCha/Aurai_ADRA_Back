package com.metaverse.aurai_adra.dto;

import java.util.List;

public class ProgressSnapshotDto {
    private String userId;
    private int totalChapters;
    private List<Integer> successes;
    private int successCount;

    // 새로 추가: optional learning view
    private LearningAgeResponse learning;

    public ProgressSnapshotDto(String userId, int totalChapters, List<Integer> successes) {
        this.userId = userId;
        this.totalChapters = totalChapters;
        this.successes = successes;
        this.successCount = successes != null ? successes.size() : 0;
    }

    // 편의 생성자: snapshot + learning
    public ProgressSnapshotDto(String userId, int totalChapters, List<Integer> successes, LearningAgeResponse learning) {
        this(userId, totalChapters, successes);
        this.learning = learning;
    }

    public String getUserId() { return userId; }
    public int getTotalChapters() { return totalChapters; }
    public List<Integer> getSuccesses() { return successes; }
    public int getSuccessCount() { return successCount; }

    public LearningAgeResponse getLearning() { return learning; }
    public void setLearning(LearningAgeResponse learning) { this.learning = learning; }
}