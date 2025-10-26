package com.metaverse.aurai_adra.controller;

import com.metaverse.aurai_adra.dto.MarkChapterRequest;
import com.metaverse.aurai_adra.dto.ProgressSnapshotDto;
import com.metaverse.aurai_adra.dto.RemoveChapterRequest;
import com.metaverse.aurai_adra.dto.LearningAgeResponse;
import com.metaverse.aurai_adra.service.ProgressService;
import com.metaverse.aurai_adra.util.LearningAgeUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    // GET /api/progress/chapters/{userId}
    @GetMapping("/chapters/{userId}")
    public ResponseEntity<ProgressSnapshotDto> getProgress(@PathVariable String userId) {
        var snap = progressService.getSnapshot(userId);
        return ResponseEntity.ok(snap);
    }

    // POST /api/progress/chapters
    @PostMapping("/chapters")
    public ResponseEntity<ProgressSnapshotDto> markChapter(@RequestBody MarkChapterRequest req) {
        if (req.getSuccess() == null || !req.getSuccess()) {
            // 성공만 저장하도록 정책 고정
            return ResponseEntity.badRequest().build();
        }
        var snap = progressService.markSuccess(req.getUserId(), req.getChapterId(), req.getAt());
        return ResponseEntity.ok(snap);
    }

    // DELETE /api/progress/chapters
    @DeleteMapping("/chapters")
    public ResponseEntity<ProgressSnapshotDto> deleteChapter(@RequestBody RemoveChapterRequest req) {
        var snap = progressService.removeSuccess(req.getUserId(), req.getChapterId());
        return ResponseEntity.ok(snap);
    }

    // (선택) GET /api/progress/learning-age/{userId}?actualAge=67
    @GetMapping("/learning-age/{userId}")
    public ResponseEntity<LearningAgeResponse> getLearningAge(
            @PathVariable String userId,
            @RequestParam("actualAge") int actualAgeYears
    ) {
        var snap = progressService.getSnapshot(userId);
        int decade = LearningAgeUtil.getLearningDecade(actualAgeYears, snap.getSuccessCount(), snap.getTotalChapters());
        String label = LearningAgeUtil.getLearningAgeLabel(decade);
        int percent = LearningAgeUtil.getProgressPercent(snap.getSuccessCount(), snap.getTotalChapters());
        return ResponseEntity.ok(new LearningAgeResponse(userId, decade, label, percent, snap.getSuccessCount(), snap.getTotalChapters()));
    }
}