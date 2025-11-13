package com.metaverse.aurai_adra.controller;

import com.metaverse.aurai_adra.dto.*;
import com.metaverse.aurai_adra.jwt.JwtTokenProvider;    // 추가
import com.metaverse.aurai_adra.repository.UserRepository; // 추가
import com.metaverse.aurai_adra.domain.User;               // 추가
import com.metaverse.aurai_adra.service.ProgressService;
import com.metaverse.aurai_adra.util.LearningAgeUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;                  // 추가
import org.springframework.web.server.ResponseStatusException; // 추가
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;            // 추가
import java.security.Principal;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;
    private final JwtTokenProvider tokenProvider;          // 추가
    private final UserRepository userRepository;           // 추가

    public ProgressController(ProgressService progressService,
                              JwtTokenProvider tokenProvider,
                              UserRepository userRepository) {
        this.progressService = progressService;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    private String resolveUserName(Principal principal, HttpServletRequest request) {
        // 1) principal 우선
        if (principal != null) {
            return principal.getName(); // 우리 토큰의 subject는 nickname
        }
        // 2) Authorization 헤더 fallback
        String authz = request.getHeader("Authorization");
        String token = tokenProvider.resolveToken(authz);
        if (token != null && tokenProvider.validateToken(token)) {
            String nickname = tokenProvider.getNicknameFromToken(token);
            // 존재하는 사용자만 허용
            return userRepository.findByNickname(nickname)
                    .map(User::getNickname)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for token"));
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    @GetMapping("/chapters/{userId}")
    public ResponseEntity<ProgressSnapshotDto> getProgress(@PathVariable String userId) {
        var snap = progressService.getSnapshot(userId);
        return ResponseEntity.ok(snap);
    }

    @GetMapping("/chapters/me")
    public ResponseEntity<ProgressSnapshotDto> getMyProgress(Principal principal, HttpServletRequest request) {
        final String tokenUserId = resolveUserName(principal, request);
        var snap = progressService.getSnapshot(tokenUserId);
        return ResponseEntity.ok(snap);
    }

    @PostMapping("/chapters")
    public ResponseEntity<ProgressSnapshotDto> markChapter(@RequestBody MarkChapterRequest req,
                                                           Principal principal,
                                                           HttpServletRequest request) {
        // principal or Authorization → nickname
        final String tokenUserId = resolveUserName(principal, request);

        Integer actualAge = req.getActualAge();
        var snap = progressService.markSuccess(
                tokenUserId,
                req.getChapterId(),
                req.getAt(),
                req.getScore(),
                req.getMeta(),
                req.getSuccess(),
                actualAge
        );
        return ResponseEntity.ok(snap);
    }

    @DeleteMapping("/chapters")
    public ResponseEntity<ProgressSnapshotDto> deleteChapter(@RequestBody RemoveChapterRequest req,
                                                             Principal principal,
                                                             HttpServletRequest request) {
        final String tokenUserId = resolveUserName(principal, request);
        var snap = progressService.removeSuccess(tokenUserId, req.getChapterId());
        return ResponseEntity.ok(snap);
    }

    @GetMapping("/practice-scores/me")
    public ResponseEntity<PracticeScoresResponse> getMyPracticeScores(
            @RequestParam(name = "appId", required = false) String appId,
            Principal principal,
            HttpServletRequest request
    ) {
        final String tokenUserId = resolveUserName(principal, request);
        var resp = progressService.getBestPracticeScores(tokenUserId, appId);
        return ResponseEntity.ok(resp);
    }

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