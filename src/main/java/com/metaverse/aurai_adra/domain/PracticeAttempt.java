package com.metaverse.aurai_adra.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "practice_attempts")
public class PracticeAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "chapter_id", nullable = false)
    private Integer chapterId;

    @Column(name = "at_ts")
    private Instant at;

    // DB에 JSON 저장: PostgreSQL이면 JSONB, MySQL이면 JSON 또는 TEXT로 변경
    @Lob
    @Column(name = "score_json", columnDefinition = "TEXT")
    private String scoreJson;

    @Lob
    @Column(name = "meta_json", columnDefinition = "TEXT")
    private String metaJson;

    public PracticeAttempt() {}

    public PracticeAttempt(String userId, Integer chapterId, Instant at, String scoreJson, String metaJson) {
        this.userId = userId;
        this.chapterId = chapterId;
        this.at = at;
        this.scoreJson = scoreJson;
        this.metaJson = metaJson;
    }

    public static PracticeAttempt of(String userId, Integer chapterId, Instant at, String scoreJson, String metaJson) {
        return new PracticeAttempt(userId, chapterId, at, scoreJson, metaJson);
    }

    // getters / setters
    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Integer getChapterId() { return chapterId; }
    public void setChapterId(Integer chapterId) { this.chapterId = chapterId; }
    public Instant getAt() { return at; }
    public void setAt(Instant at) { this.at = at; }
    public String getScoreJson() { return scoreJson; }
    public void setScoreJson(String scoreJson) { this.scoreJson = scoreJson; }
    public String getMetaJson() { return metaJson; }
    public void setMetaJson(String metaJson) { this.metaJson = metaJson; }
}