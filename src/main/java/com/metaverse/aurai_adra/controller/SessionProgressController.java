package com.metaverse.aurai_adra.controller;

import com.metaverse.aurai_adra.domain.User;
import com.metaverse.aurai_adra.domain.UserSessionProgress;
import com.metaverse.aurai_adra.dto.AppSessionProgressRequest;
import com.metaverse.aurai_adra.dto.AppProgressSummaryDto;
import com.metaverse.aurai_adra.service.SessionProgressService;
import com.metaverse.aurai_adra.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/progress")
public class SessionProgressController {

    private final SessionProgressService progressService;
    private final UserRepository userRepository;

    public SessionProgressController(SessionProgressService progressService, UserRepository userRepository) {
        this.progressService = progressService;
        this.userRepository = userRepository;
    }

    private Long resolveUserId(Principal principal) {
        if (principal == null) throw new RuntimeException("Unauthorized");
        String name = principal.getName();
        // Jwt subject is nickname in existing JwtTokenProvider
        try {
            // try numeric id fallback
            return Long.parseLong(name);
        } catch (NumberFormatException nfe) {
            Optional<User> u = userRepository.findByNickname(name);
            if (u.isPresent()) return u.get().getId();
            throw new RuntimeException("User not found for principal: " + name);
        }
    }

    @PostMapping("/session")
    public ResponseEntity<?> markSession(@RequestBody AppSessionProgressRequest req, Principal principal) {
        Long userId = resolveUserId(principal);
        LocalDateTime at = null;
        if (req.getAt() != null) {
            try { at = LocalDateTime.parse(req.getAt()); } catch (Exception e) { /* ignore malformed */ }
        }
        UserSessionProgress saved = progressService.upsertProgress(userId, req.getAppId(), req.getSessionId(), req.getType(), at, req.getMetadata());
        // return per-app summary for convenience
        Map<String, AppProgressSummaryDto> map = progressService.mapSummary(userId);
        AppProgressSummaryDto summary = map.getOrDefault(req.getAppId(), new AppProgressSummaryDto());
        return ResponseEntity.ok(Map.of("ok", true, "summary", summary));
    }

    /**
     * Return only the per-app/session snapshot for the current user.
     */
    @GetMapping("/sessions/me")
    public ResponseEntity<?> getMySessionsSnapshot(Principal principal) {
        Long userId = resolveUserId(principal);
        Map<String, AppProgressSummaryDto> map = progressService.mapSummary(userId);
        return ResponseEntity.ok(Map.of("appProgress", map));
    }
}