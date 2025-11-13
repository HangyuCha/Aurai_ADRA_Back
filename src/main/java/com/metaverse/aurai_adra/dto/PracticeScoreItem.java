package com.metaverse.aurai_adra.dto;

public class PracticeScoreItem {
    private Integer chapterId;
    private Integer total; // 해당 챕터의 최고 total 점수 (null 가능)

    public PracticeScoreItem() {}

    public PracticeScoreItem(Integer chapterId, Integer total) {
        this.chapterId = chapterId;
        this.total = total;
    }

    public Integer getChapterId() { return chapterId; }
    public void setChapterId(Integer chapterId) { this.chapterId = chapterId; }

    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
}