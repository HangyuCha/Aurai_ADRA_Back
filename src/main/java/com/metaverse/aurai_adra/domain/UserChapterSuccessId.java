package com.metaverse.aurai_adra.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserChapterSuccessId implements Serializable {
    private String userId;
    private Integer chapterId;

    public UserChapterSuccessId() {}

    public UserChapterSuccessId(String userId, Integer chapterId) {
        this.userId = userId;
        this.chapterId = chapterId;
    }

    public String getUserId() { return userId; }
    public Integer getChapterId() { return chapterId; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setChapterId(Integer chapterId) { this.chapterId = chapterId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserChapterSuccessId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(chapterId, that.chapterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, chapterId);
    }
}