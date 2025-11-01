package com.metaverse.aurai_adra.dto;

import java.util.Map;

public class MarkChapterRequest {
    private String userId;
    private Integer chapterId;
    private Boolean success;
    private String at; // ISO8601 optional

    // 새 필드: score, meta
    private Map<String, Object> score;
    private Map<String, Object> meta;

    public String getUserId() { return userId; }
    public Integer getChapterId() { return chapterId; }
    public Boolean getSuccess() { return success; }
    public String getAt() { return at; }
    public Map<String, Object> getScore() { return score; }
    public Map<String, Object> getMeta() { return meta; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setChapterId(Integer chapterId) { this.chapterId = chapterId; }
    public void setSuccess(Boolean success) { this.success = success; }
    public void setAt(String at) { this.at = at; }
    public void setScore(Map<String, Object> score) { this.score = score; }
    public void setMeta(Map<String, Object> meta) { this.meta = meta; }
}