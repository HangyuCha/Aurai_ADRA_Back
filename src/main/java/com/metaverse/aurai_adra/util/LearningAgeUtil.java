package com.metaverse.aurai_adra.util;

public final class LearningAgeUtil {
    private LearningAgeUtil() {}

    public static int toDecade(int actualAge) {
        int age = Math.max(0, actualAge);
        int decade = (age / 10) * 10;
        return Math.max(10, decade);
    }

    public static int getLearningDecade(int actualAgeYears, int successCount, int totalChapters) {
        int startDecade = toDecade(actualAgeYears);
        int minDecade = 20; // 변경: 최소 20대로 고정
        int steps = Math.max(0, (startDecade - minDecade) / 10);
        if (steps == 0) return Math.max(startDecade, minDecade);

        int total = Math.max(1, totalChapters);
        int done = Math.max(0, successCount);

        double perStep = (double) total / (double) steps;
        int stepIndex = (int) Math.floor(done / perStep);
        if (stepIndex > steps) stepIndex = steps;
        return startDecade - (10 * stepIndex);
    }

    public static String getLearningAgeLabel(int decade) {
        int d = Math.max(20, (decade / 10) * 10); // 변경: 라벨 최소 20대
        return d + "대";
    }

    public static int getProgressPercent(int successCount, int totalChapters) {
        int total = Math.max(1, totalChapters);
        int done = Math.max(0, successCount);
        return Math.min(100, (int) Math.round((done * 100.0) / total));
    }
}