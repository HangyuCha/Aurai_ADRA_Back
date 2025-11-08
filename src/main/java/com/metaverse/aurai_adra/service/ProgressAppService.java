package com.metaverse.aurai_adra.service;

import com.metaverse.aurai_adra.domain.UserAppProgress;
import com.metaverse.aurai_adra.dto.AppProgressSummaryDto;
import com.metaverse.aurai_adra.repository.UserAppProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProgressAppService {
    private final UserAppProgressRepository repo;

    public ProgressAppService(UserAppProgressRepository repo) {
        this.repo = repo;
    }

    /**
     * Upsert per-app progress and return a DTO summary.
     * Idempotent: if flag already true, it will ensure timestamps set (or updated).
     */
    @Transactional
    public AppProgressSummaryDto upsertAppProgress(Long userId, String appId, String type, LocalDateTime at) {
        UserAppProgress p = repo.findByUserIdAndAppId(userId, appId)
                .orElseGet(() -> {
                    UserAppProgress n = new UserAppProgress();
                    n.setUserId(userId);
                    n.setAppId(appId);
                    return n;
                });

        LocalDateTime now = (at == null) ? LocalDateTime.now() : at;

        if ("practice".equalsIgnoreCase(type)) {
            p.setPracticeDone(true);
            p.setLastPracticeAt(now);
        } else if ("learn".equalsIgnoreCase(type) || "learning".equalsIgnoreCase(type)) {
            p.setLearnDone(true);
            p.setLastLearnAt(now);
        } else {
            throw new IllegalArgumentException("unknown type: " + type);
        }

        UserAppProgress saved = repo.save(p);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<UserAppProgress> listByUser(Long userId) {
        return repo.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Map<String, AppProgressSummaryDto> mapSummary(Long userId) {
        List<UserAppProgress> list = listByUser(userId);
        if (list == null || list.isEmpty()) return Collections.emptyMap();
        return list.stream().collect(Collectors.toMap(
                UserAppProgress::getAppId,
                this::toDto
        ));
    }

    // Helper to convert entity -> DTO in one place
    private AppProgressSummaryDto toDto(UserAppProgress p) {
        AppProgressSummaryDto dto = new AppProgressSummaryDto();
        dto.setAppId(p.getAppId());
        dto.setPracticeDone(p.isPracticeDone());
        dto.setLearnDone(p.isLearnDone());
        dto.setLastPracticeAt(p.getLastPracticeAt() == null ? null : p.getLastPracticeAt().toString());
        dto.setLastLearnAt(p.getLastLearnAt() == null ? null : p.getLastLearnAt().toString());

        // If your AppProgressSummaryDto contains extra collections or metadata, set them here
        // For example:
        // dto.setSomeListA(p.getSomeListA());
        // dto.setSomeMapB(p.getSomeMapB());

        return dto;
    }
}