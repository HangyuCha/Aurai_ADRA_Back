package com.metaverse.aurai_adra.controller;

import com.metaverse.aurai_adra.dto.AppProgressRequest;
import com.metaverse.aurai_adra.dto.AppProgressSummaryDto;
import com.metaverse.aurai_adra.service.ProgressAppService;
import com.metaverse.aurai_adra.repository.UserRepository; // optional: for resolving principal -> user id
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class ProgressAppController {

    private final ProgressAppService service;
    private final UserRepository userRepository; // project has this repo; used only if Principal.getName() not numeric

    public ProgressAppController(ProgressAppService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @PostMapping("/app")
    public ResponseEntity<?> upsertApp(@RequestBody AppProgressRequest req, Principal principal) {
        Long userId = resolveUserId(principal);
        LocalDateTime at = null;
        try {
            if (req.getAt() != null) at = LocalDateTime.parse(req.getAt());
        } catch (DateTimeParseException ignored) {}

        service.upsertAppProgress(userId, req.getAppId(), req.getType(), at);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @GetMapping("/summary/me")
    public ResponseEntity<?> getSummaryForMe(Principal principal) {
        Long userId = resolveUserId(principal);
        Map<String, AppProgressSummaryDto> m = service.mapSummary(userId);
        return ResponseEntity.ok(Map.of("userId", userId, "appProgress", m));
    }

    private Long resolveUserId(Principal principal) {
        // 기본: Principal.getName()가 숫자인 경우 바로 userId로 사용
        // 상황에 따라 아래 코드를 프로젝트 맞게 수정하세요.
        String name = principal == null ? null : principal.getName();
        if (name == null) throw new IllegalStateException("No principal available");

        try {
            return Long.parseLong(name);
        } catch (NumberFormatException ex) {
            // TODO: Principal 이름이 숫자가 아니라면 실제 사용자 조회 로직으로 바꾸세요.
            // 예: userRepository.findByUsername(name).orElseThrow(...)
            throw new IllegalStateException("Principal name is not numeric. Update resolveUserId() to map principal to user id.");
        }
    }
}