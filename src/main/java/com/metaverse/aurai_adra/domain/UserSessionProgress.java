package com.metaverse.aurai_adra.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_session_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","app_id","session_id","progress_type"}))
public class UserSessionProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "app_id", nullable = false, length = 100)
    private String appId;

    @Column(name = "session_id", nullable = false, length = 200)
    private String sessionId;

    @Column(name = "progress_type", nullable = false, length = 32)
    private String progressType; // 'learn' | 'practice'

    @Column(nullable = false)
    private Boolean completed = Boolean.TRUE;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // MySQL JSON 컬럼을 사용합니다. columnDefinition을 사용하면 Hibernate가 JSON으로 처리합니다.
    @Column(columnDefinition = "json")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserSessionProgress() {}

    public UserSessionProgress(Long userId, String appId, String sessionId, String progressType, Boolean completed, LocalDateTime completedAt, String metadata) {
        this.userId = userId;
        this.appId = appId;
        this.sessionId = sessionId;
        this.progressType = progressType;
        this.completed = completed;
        this.completedAt = completedAt;
        this.metadata = metadata;
    }

    // getters / setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getProgressType() { return progressType; }
    public void setProgressType(String progressType) { this.progressType = progressType; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}