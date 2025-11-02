package com.metaverse.aurai_adra.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metaverse.aurai_adra.domain.PracticeAttempt;
import com.metaverse.aurai_adra.domain.UserChapterSuccess;
import com.metaverse.aurai_adra.domain.UserChapterSuccessId;
import com.metaverse.aurai_adra.domain.User;
import com.metaverse.aurai_adra.dto.AppProgressSummaryDto;
import com.metaverse.aurai_adra.dto.LearningAgeResponse;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;

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
                // don't fail snapshot construction if app progress retrieval fails
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
    public ProgressSnapshotDto markSuccess(String userId, Integer chapterId, String atIso8601, Map<String, Object> score, Map<String, Object> meta, Boolean success, Integer actualAge) {
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
                // don't fail the main flow if persistence to account fails; log in real app
                // e.g. logger.warn("failed to persist learning age", ex);
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

    private void validateChapterId(Integer chapterId) {
        if (chapterId == null || chapterId < 1 || chapterId > TOTAL_CHAPTERS) {
            throw new IllegalArgumentException("chapterId must be between 1 and " + TOTAL_CHAPTERS);
        }
    }

    private Instant parseInstantOrNow(String iso) {
        if (iso == null || iso.isBlank()) return Instant.now();
        try { return Instant.parse(iso); } catch (DateTimeParseException e) { return Instant.now(); }
    }

    /**
     * Try to resolve a String userId (token name) into a numeric Long id if possible.
     * Uses numeric parse first, else attempts to find by nickname via userRepository.
     * Returns null if not resolvable.
     */
    private Long resolveUserIdToLong(String userId) {
        if (userId == null) return null;
        try {
            return Long.parseLong(userId);
        } catch (NumberFormatException ex) {
            try {
                return userRepository.findByNickname(userId).map(User::getId).orElse(null);
            } catch (Exception e) {
                return null;
            }
        }
    }
}