package com.metaverse.aurai_adra.dto;

public class RemoveChapterRequest {
    private String userId;
    private Integer chapterId;

    public String getUserId() { return userId; }
    public Integer getChapterId() { return chapterId; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setChapterId(Integer chapterId) { this.chapterId = chapterId; }
}