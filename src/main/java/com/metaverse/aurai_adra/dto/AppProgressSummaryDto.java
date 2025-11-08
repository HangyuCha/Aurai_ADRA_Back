package com.metaverse.aurai_adra.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * App-level progress summary DTO.
 * - appId, practice/learn flags, last timestamps (ISO strings)
 * - sessions: map of sessionId -> (map of progressType -> SessionProgressDto)
 */
public class AppProgressSummaryDto implements Serializable {

    private String appId;
    private boolean practiceDone;
    private boolean learnDone;
    private String lastPracticeAt;
    private String lastLearnAt;

    // 새로 추가: per-session progress map
    // sessions.get(sessionId).get("learn"|"practice") => SessionProgressDto
    private Map<String, Map<String, SessionProgressDto>> sessions;

    public AppProgressSummaryDto() {
        // 빈 맵으로 초기화하여 getSessions() 호출 시 NPE 방지
        this.sessions = new HashMap<>();
    }

    /**
     * 기존에 사용되던 5-인자 생성자(호출처 호환을 위해 유지)
     */
    public AppProgressSummaryDto(String appId, boolean practiceDone, boolean learnDone,
                                 String lastPracticeAt, String lastLearnAt) {
        this();
        this.appId = appId;
        this.practiceDone = practiceDone;
        this.learnDone = learnDone;
        this.lastPracticeAt = lastPracticeAt;
        this.lastLearnAt = lastLearnAt;
    }

    // 필요하면 전 필드 생성자/빌더를 추가할 수 있음

    // getters / setters

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isPracticeDone() {
        return practiceDone;
    }

    public void setPracticeDone(boolean practiceDone) {
        this.practiceDone = practiceDone;
    }

    public boolean isLearnDone() {
        return learnDone;
    }

    public void setLearnDone(boolean learnDone) {
        this.learnDone = learnDone;
    }

    public String getLastPracticeAt() {
        return lastPracticeAt;
    }

    public void setLastPracticeAt(String lastPracticeAt) {
        this.lastPracticeAt = lastPracticeAt;
    }

    public String getLastLearnAt() {
        return lastLearnAt;
    }

    public void setLastLearnAt(String lastLearnAt) {
        this.lastLearnAt = lastLearnAt;
    }

    public Map<String, Map<String, SessionProgressDto>> getSessions() {
        // 안전을 위해 null이면 빈 맵 반환 (하지만 기본 생성자에서 이미 초기화됨)
        if (this.sessions == null) this.sessions = new HashMap<>();
        return sessions;
    }

    public void setSessions(Map<String, Map<String, SessionProgressDto>> sessions) {
        this.sessions = sessions;
    }

    // equals / hashCode / toString (간단 구현)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppProgressSummaryDto that = (AppProgressSummaryDto) o;
        return practiceDone == that.practiceDone &&
                learnDone == that.learnDone &&
                Objects.equals(appId, that.appId) &&
                Objects.equals(lastPracticeAt, that.lastPracticeAt) &&
                Objects.equals(lastLearnAt, that.lastLearnAt) &&
                Objects.equals(sessions, that.sessions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, practiceDone, learnDone, lastPracticeAt, lastLearnAt, sessions);
    }

    @Override
    public String toString() {
        return "AppProgressSummaryDto{" +
                "appId='" + appId + '\'' +
                ", practiceDone=" + practiceDone +
                ", learnDone=" + learnDone +
                ", lastPracticeAt='" + lastPracticeAt + '\'' +
                ", lastLearnAt='" + lastLearnAt + '\'' +
                ", sessions=" + sessions +
                '}';
    }
}