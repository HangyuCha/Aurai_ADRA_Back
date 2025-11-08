package com.metaverse.aurai_adra.dto;

public class SessionProgressDto {
    private boolean completed;
    private String completedAt; // ISO string
    private Object metadata; // can be Map or primitive

    public SessionProgressDto() {}

    public SessionProgressDto(boolean completed, String completedAt, Object metadata) {
        this.completed = completed;
        this.completedAt = completedAt;
        this.metadata = metadata;
    }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public Object getMetadata() { return metadata; }
    public void setMetadata(Object metadata) { this.metadata = metadata; }
}