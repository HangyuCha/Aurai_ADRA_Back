package com.metaverse.aurai_adra.dto;

public class AppProgressSummaryDto {
    private String appId;
    private boolean practiceDone;
    private boolean learnDone;
    private String lastPracticeAt;
    private String lastLearnAt;

    public AppProgressSummaryDto() {}

    public AppProgressSummaryDto(String appId, boolean practiceDone, boolean learnDone, String lastPracticeAt, String lastLearnAt) {
        this.appId = appId;
        this.practiceDone = practiceDone;
        this.learnDone = learnDone;
        this.lastPracticeAt = lastPracticeAt;
        this.lastLearnAt = lastLearnAt;
    }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public boolean isPracticeDone() { return practiceDone; }
    public void setPracticeDone(boolean practiceDone) { this.practiceDone = practiceDone; }

    public boolean isLearnDone() { return learnDone; }
    public void setLearnDone(boolean learnDone) { this.learnDone = learnDone; }

    public String getLastPracticeAt() { return lastPracticeAt; }
    public void setLastPracticeAt(String lastPracticeAt) { this.lastPracticeAt = lastPracticeAt; }

    public String getLastLearnAt() { return lastLearnAt; }
    public void setLastLearnAt(String lastLearnAt) { this.lastLearnAt = lastLearnAt; }
}