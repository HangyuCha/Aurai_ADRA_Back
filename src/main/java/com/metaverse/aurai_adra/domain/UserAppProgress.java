package com.metaverse.aurai_adra.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_app_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "app_id"}))
public class UserAppProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "app_id", nullable = false, length = 64)
    private String appId;

    @Column(name = "practice_done")
    private boolean practiceDone;

    @Column(name = "learn_done")
    private boolean learnDone;

    @Column(name = "last_practice_at")
    private LocalDateTime lastPracticeAt;

    @Column(name = "last_learn_at")
    private LocalDateTime lastLearnAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UserAppProgress() {}

    @PrePersist
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public boolean isPracticeDone() { return practiceDone; }
    public void setPracticeDone(boolean practiceDone) { this.practiceDone = practiceDone; }

    public boolean isLearnDone() { return learnDone; }
    public void setLearnDone(boolean learnDone) { this.learnDone = learnDone; }

    public LocalDateTime getLastPracticeAt() { return lastPracticeAt; }
    public void setLastPracticeAt(LocalDateTime lastPracticeAt) { this.lastPracticeAt = lastPracticeAt; }

    public LocalDateTime getLastLearnAt() { return lastLearnAt; }
    public void setLastLearnAt(LocalDateTime lastLearnAt) { this.lastLearnAt = lastLearnAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}