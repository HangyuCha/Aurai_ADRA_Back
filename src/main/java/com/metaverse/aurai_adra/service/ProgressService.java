package com.metaverse.aurai_adra.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metaverse.aurai_adra.domain.PracticeAttempt;
import com.metaverse.aurai_adra.domain.UserChapterSuccess;
import com.metaverse.aurai_adra.domain.UserChapterSuccessId;
import com.metaverse.aurai_adra.domain.User;
import com.metaverse.aurai_adra.dto.AppProgressSummaryDto;
import com.metaverse.aurai_adra.dto.LearningAgeResponse;
import com.metaverse.aurai_adra.dto.PracticeScoreItem;
import com.metaverse.aurai_adra.dto.PracticeScoresResponse;
import com.metaverse.aurai_adra.dto.ProgressSnapshotDto;
import com.metaverse.aurai_adra.repository.PracticeAttemptRepository;
import com.metaverse.aurai_adra.repository.UserChapterSuccessRepository;
import com.metaverse.aurai_adra.repository.UserRepository;
import com.metaverse.aurai_adra.util.LearningAgeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    private final UserChapterSuccessRepository repo;
    // 총 챕터 수: 서비스 정책상 20으로 고정
    private static final int TOTAL_CHAPTERS = 20;

    private final PracticeAttemptRepository attemptRepo;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final ProgressAppService progressAppService;

    public ProgressService(UserChapterSuccessRepository repo,
                           PracticeAttemptRepository attemptRepo,
                           ObjectMapper objectMapper,
                           UserRepository userRepository,
                           ProgressAppService progressAppService) {
        this.repo = repo;
        this.attemptRepo = attemptRepo;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.progressAppService = progressAppService;
    }

    public int getTotalChapters() { return TOTAL_CHAPTERS; }

    @Transactional(readOnly = true)
    public ProgressSnapshotDto getSnapshot(String userId) {
        var list = repo.findByIdUserIdOrderByIdChapterIdAsc(userId);
        var successes = list.stream()
                .map(e -> e.getId().getChapterId())
                .sorted(Comparator.naturalOrder())
                .toList();

        ProgressSnapshotDto snapshot = new ProgressSnapshotDto(userId, TOTAL_CHAPTERS, successes);

        // Attach per-app progress if we can resolve a numeric user id
        Long numericUserId = resolveUserIdToLong(userId);
        if (numericUserId != null) {
            try {
                Map<String, AppProgressSummaryDto> appProgress = progressAppService.mapSummary(numericUserId);
                snapshot.setAppProgress(appProgress);
            } catch (Exception ex) {
                snapshot.setAppProgress(Collections.emptyMap());
            }
        } else {
            snapshot.setAppProgress(Collections.emptyMap());
        }

        return snapshot;
    }

    /**
     * markSuccess 확장:
     * - 항상 practice_attempts에 시도 저장 (score/meta를 JSON으로 저장)
     * - success == true이면 user_chapter_success에 최초 성공 기록
     * - optional actualAge가 들어오면 snapshot에 learning view를 포함해서 반환
     * - optional: learning view를 account(user) 테이블에 영구 저장 (userRepository 사용)
     */
    @Transactional
    public ProgressSnapshotDto markSuccess(
            String userId,
            Integer chapterId,
            String atIso8601,
            Map<String, Object> score,
            Map<String, Object> meta,
            Boolean success,
            Integer actualAge
    ) {
        validateChapterId(chapterId);
        Instant at = parseInstantOrNow(atIso8601);

        // 1) store attempt record (analytics)
        try {
            String scoreJson = score != null ? objectMapper.writeValueAsString(score) : null;
            String metaJson = meta != null ? objectMapper.writeValueAsString(meta) : null;
            var attempt = PracticeAttempt.of(userId, chapterId, at, scoreJson, metaJson);
            attemptRepo.save(attempt);
        } catch (JsonProcessingException e) {
            var attempt = PracticeAttempt.of(userId, chapterId, at, null, null);
            attemptRepo.save(attempt);
        }

        // 2) If success flagged, record summary (only first-time success)
        if (Boolean.TRUE.equals(success)) {
            var id = new UserChapterSuccessId(userId, chapterId);
            if (!repo.existsById(id)) {
                repo.save(UserChapterSuccess.of(userId, chapterId, at)); // 최초 성공만 기록
            }
        }

        // 3) build snapshot and optionally compute learning view
        ProgressSnapshotDto snapshot = getSnapshot(userId);

        if (actualAge != null) {
            int decade = LearningAgeUtil.getLearningDecade(actualAge, snapshot.getSuccessCount(), snapshot.getTotalChapters());
            String label = LearningAgeUtil.getLearningAgeLabel(decade);
            int percent = LearningAgeUtil.getProgressPercent(snapshot.getSuccessCount(), snapshot.getTotalChapters());
            LearningAgeResponse learning = new LearningAgeResponse(userId, decade, label, percent, snapshot.getSuccessCount(), snapshot.getTotalChapters());
            snapshot.setLearning(learning);

            // Try to persist to account (User entity) — robust handling: numeric id or nickname
            try {
                Long idLong = null;
                try { idLong = Long.parseLong(userId); } catch (NumberFormatException e) { /* not numeric */ }

                Optional<User> maybeUser = Optional.empty();
                if (idLong != null) {
                    maybeUser = userRepository.findById(idLong);
                } else {
                    maybeUser = userRepository.findByNickname(userId);
                }

                maybeUser.ifPresent(u -> {
                    u.setLearningAgeDecade(decade);
                    u.setLearningAgePercent(percent);
                    u.setLearningAgeUpdatedAt(LocalDateTime.now(ZoneId.systemDefault()));
                    userRepository.save(u);
                });
            } catch (Exception ex) {
                // don't fail the main flow if persistence to account fails
            }
        }

        return snapshot;
    }

    @Transactional
    public ProgressSnapshotDto removeSuccess(String userId, Integer chapterId) {
        validateChapterId(chapterId);
        var id = new UserChapterSuccessId(userId, chapterId);
        repo.findById(id).ifPresent(repo::delete);
        return getSnapshot(userId);
    }

    @Transactional(readOnly = true)
    public PracticeScoresResponse getBestPracticeScores(String userId, String appId) {
        // 1) attempts 가져오기
        List<PracticeAttempt> attempts = attemptRepo.findByUserId(userId);

        // 2) appId별 챕터 범위 계산
        int start = 1, end = TOTAL_CHAPTERS;
        if (appId != null && !appId.isBlank()) {
            int[] range = chapterRangeForApp(appId);
            start = range[0];
            end = range[1];
        }
        final int from = start;
        final int to = end;

        // 3) 범위 필터 후 챕터별 최고 total 집계
        Map<Integer, Integer> bestTotals = attempts.stream()
                .filter(a -> a.getChapterId() != null && a.getChapterId() >= from && a.getChapterId() <= to)
                .collect(Collectors.groupingBy(
                        PracticeAttempt::getChapterId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .map(this::extractTotalScoreSafely)
                              