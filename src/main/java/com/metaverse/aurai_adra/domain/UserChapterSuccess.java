package com.metaverse.aurai_adra.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "user_chapter_successes",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_chapter", columnNames = {"user_id", "chapter_id"})
)
public class UserChapterSuccess {

    @EmbeddedId
    private UserChapterSuccessId id;

    @Column(name = "succeeded_at", nullable = false)
    private Instant succeededAt = Instant.now();

    public UserChapterSuccess() {}

    public UserChapterSuccess(UserChapterSuccessId id, Instant succeededAt) {
        this.id = id;
        this.succeededAt = succeededAt != null ? succeededAt : Instant.now();
    }

    public static UserChapterSuccess of(String userId, Integer chapterId, Instant at) {
        return new UserChapterSuccess(new UserChapterSuccessId(userId, chapterId), at);
    }

    public UserChapterSuccessId getId() { return id; }
    public Instant getSucceededAt() { return succeededAt; }

    public void setId(UserChapterSuccessId id) { this.id = id; }
    public void setSucceededAt(Instant succeededAt) { this.succeededAt = succeededAt; }
}