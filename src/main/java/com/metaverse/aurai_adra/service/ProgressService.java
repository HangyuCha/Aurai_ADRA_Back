package com.metaverse.aurai_adra.service;

import com.metaverse.aurai_adra.domain.UserChapterSuccess;
import com.metaverse.aurai_adra.domain.UserChapterSuccessId;
import com.metaverse.aurai_adra.dto.ProgressSnapshotDto;
import com.metaverse.aurai_adra.repository.UserChapterSuccessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

@Service
public class ProgressService {

    private final UserChapterSuccessRepository repo;
    // 총 챕터 수: 서비스 정책상 20으로 고정
    private static final int TOTAL_CHAPTERS = 20;

    public ProgressService(UserChapterSuccessRepository repo) {
        this.repo = repo;
    }

    public int getTotalChapters() { return TOTAL_CHAPTERS; }

    @Transactional(readOnly = true)
    public ProgressSnapshotDto getSnapshot(String userId) {
        var list = repo.findByIdUserIdOrderByIdChapterIdAsc(userId);
        var successes = list.stream()
                .map(e -> e.getId().getChapterId())
                .sorted(Comparator.naturalOrder())
                .toList();
        return new ProgressSnapshotDto(userId, TOTAL_CHAPTERS, successes);
    }

    @Transactional
    public ProgressSnapshotDto markSuccess(String userId, Integer chapterId, String atIso8601) {
        validateChapterId(chapterId);
        var id = new UserChapterSuccessId(userId, chapterId);
        if (!repo.existsById(id)) {
            Instant at = parseInstantOrNow(atIso8601);
            repo.save(UserChapterSuccess.of(userId, chapterId, at)); // 최초 성공만 기록
        }
        return getSnapshot(userId);
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
}