package com.metaverse.aurai_adra.controller;

import com.metaverse.aurai_adra.domain.User;
import com.metaverse.aurai_adra.dto.AppProgressRequest;
import com.metaverse.aurai_adra.dto.AppProgressSummaryDto;
import com.metaverse.aurai_adra.service.ProgressAppService;
import com.metaverse.aurai_adra.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/progress")
public class ProgressAppController {

    private final ProgressAppService service;
    private final UserRepository userRepository; // 필요시 Principal -> user id 매핑용

    public ProgressAppController(ProgressAppService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    /**
     * Upsert per-app progress for the authenticated user.
     * Request example:
     * { "appId":"sms", "type":"learn", "at":"2025-11-02T10:23:00" }  // "at" optional, ISO-8601 local datetime
     */
    @PostMapping("/app")
    public ResponseEntity<?> upsertApp(@RequestBody AppProgressRequest req, Principal principal) {
        Long userId = resolveUserId(principal);
        LocalDateTime at = null;
        try {
            if (req.getAt() != null) at = LocalDateTime.parse(req.getAt());
        } catch (DateTimeParseException ignored) {}

        AppProgressSummaryDto updated = service.upsertAppProgress(userId, req.getAppId(), req.getType(), at);
        return ResponseEntity.ok(updated);
    }

    /**
     * Return per-app summary map for the authenticated user.
     */
    @GetMapping("/summary/me")
    public ResponseEntity<?> getSummaryForMe(Principal principal) {
        Long userId = resolveUserId(principal);
        Map<String, AppProgressSummaryDto> m = service.mapSummary(userId);
        return ResponseEntity.ok(Map.of("userId", userId, "appProgress", m));
    }

    /**
     * Principal.getName()가 숫자이면 그대로 userId로 사용.
     * 숫자가 아닐 경우 UserRepository.findByNickname(name)으로 매핑 시도.
     * 실패하면 IllegalStateException을 던집니다(필요시 401 처리로 바꾸세요).
     */
    private Long resolveUserId(Principal principal) {
        String name = principal == null ? null : principal.getName();
        if (name == null) throw new IllegalStateException("No principal available");

        try {
            return Long.parseLong(name);
        } catch (NumberFormatException ex) {
            // Principal 이름이 숫자가 아니라면 UserRepository로 매핑 시도
            try {
                Optional<User> maybe = userRepository.findByNickname(name);
                if (maybe != null && maybe.isPresent()) {
                    return maybe.get().getId();
                }
            } catch (Exception e) {
                // lookup failed; will throw below
            }
            throw new IllegalStateException("Principal name is not numeric and user lookup failed. Update resolveUserId() to map principal to user id.");
        }
    }
}