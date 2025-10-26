package com.metaverse.aurai_adra.dto;

public class MarkChapterRequest {
    private String userId;
    private Integer chapterId;
    private Boolean success;
    private String at; // ISO8601 optional

    public String getUserId() { return userId; }
    public Integer getChapterId() { return chapterId; }
    public Boolean getSuccess() { return success; }
    public String getAt() { return at; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setChapterId(Integer chapterId) { this.chapterId = chapterId; }
    public void setSuccess(Boolean success) { this.success = success; }
    public void setAt(String at) { this.at = at; }
}