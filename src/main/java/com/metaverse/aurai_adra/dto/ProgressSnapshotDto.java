package com.metaverse.aurai_adra.dto;

import java.util.List;

public class ProgressSnapshotDto {
    private String userId;
    private int totalChapters;
    private List<Integer> successes;
    private int successCount;

    public ProgressSnapshotDto(String userId, int totalChapters, List<Integer> successes) {
        this.userId = userId;
        this.totalChapters = totalChapters;
        this.successes = successes;
        this.successCount = successes != null ? successes.size() : 0;
    }

    public String getUserId() { return userId; }
    public int getTotalChapters() { return totalChapters; }
    public List<Integer> getSuccesses() { return successes; }
    public int getSuccessCount() { return successCount; }
}