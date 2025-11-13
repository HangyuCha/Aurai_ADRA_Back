package com.metaverse.aurai_adra.controller;

import com.metaverse.aurai_adra.domain.User;
import com.metaverse.aurai_adra.domain.UserSessionProgress;
import com.metaverse.aurai_adra.dto.AppSessionProgressRequest;
import com.metaverse.aurai_adra.dto.AppProgressSummaryDto;
import com.metaverse.aurai_adra.service.SessionProgressService;
import com.metaverse.aurai_adra.repository.UserRepository;
import com.metaverse.aurai_adra.jwt.JwtTokenProvider; // 추가
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest; // 주의: jakarta 버전 사용
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class SessionProgressController {

    private final SessionProgressService progressService;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider; // 추가

    public SessionProgressController(SessionProgressService progressService,
                                     UserRepository userRepository,
                                     JwtTokenProvider tokenProvider) {
        this.progressService = progressService;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    private Long resolveUserId(Principal principal, HttpServletRequest request) {
        // 1) Principal 우선
        if (principal != null) {
            String name = principal.getName(); // JwtTokenProvider가 subject로 nickname을 넣으므로 여기엔 nickname이 들어옵니다.
            // 숫자면 그대로 ID, 아니면 nickname으로 조회
            try { return Long.parseLong(name); } catch (NumberFormatException ignore) { /* nickname fallback */ }
            return userRepository.findByNickname(name)
                    .map(User::getId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for principal"));
        }

        // 2) Authorization 헤더 fallback
        String authz = request.getHeader("Authorization");
        String token = tokenProvider.resolveToken(authz);
        if (token != null && tokenProvider.validateToken(token)) {
            String nickname = tokenProvider.getNicknameFromToken(token);
            return userRepository.findByNickname(nickname)
                    .map(User::getId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for token"));
        }

        // 3) 모두 실패 → 401
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    @PostMapping("/session")
    public ResponseEntity<?> markSession(@RequestBody AppSessionProgressRequest req,
                                         Principal principal,
                                         HttpServletRequest request) {
        Long userId = resolveUserId(principal, request);
        LocalDateTime at = null;
        if (req.getAt() != null) {
            try { at = LocalDateTime.parse(req.getAt()); } catch (Exception ignore) {}
        }
        UserSessionProgress saved = progressService.upsertProgress(
                userId, req.getAppId(), req.getSessionId(), req.getType(), at, req.getMetadata()
        );
        Map<String, AppProgressSummaryDto> map = progressService.mapSummary(userId);
        AppProgressSummaryDto summary = map.getOrDefault(req.getAppId(), new AppProgressSummaryDto());
        return ResponseEntity.ok(Map.of("ok", true, "summary", summary));
    }

    @GetMapping("/sessions/me")
    public ResponseEntity<?> getMySessionsSnapshot(Principal principal, HttpServletRequest request) {
        Long userId = resolveUserId(principal, request);
        Map<String, AppProgressSummaryDto> map = progressService.mapSummary(userId);
        return ResponseEntity.ok(Map.of("appProgress", map));
    }
}