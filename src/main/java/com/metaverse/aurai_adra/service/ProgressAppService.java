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

        if ("practice".equalsIgnoreCase(type)) {
            p.setPracticeDone(true);
            p.setLastPracticeAt(at == null ? LocalDateTime.now() : at);
        } else if ("learn".equalsIgnoreCase(type) || "learning".equalsIgnoreCase(type)) {
            p.setLearnDone(true);
            p.setLastLearnAt(at == null ? LocalDateTime.now() : at);
        } else {
            throw new IllegalArgumentException("unknown type: " + type);
        }

        UserAppProgress saved = repo.save(p);

        return new AppProgressSummaryDto(
                saved.getAppId(),
                saved.isPracticeDone(),
                saved.isLearnDone(),
                saved.getLastPracticeAt() == null ? null : saved.getLastPracticeAt().toString(),
                saved.getLastLearnAt() == null ? null : saved.getLastLearnAt().toString()
        );
    }

    @Transactional(readOnly = true)
    public List<UserAppProgress> listByUser(Long userId) {
        return repo.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Map<String, AppProgressSummaryDto> mapSummary(Long userId) {
        List<UserAppProgress> list = listByUser(userId);
        if (list == null) return Collections.emptyMap();
        return list.stream().collect(Collectors.toMap(
                UserAppProgress::getAppId,
                p -> new AppProgressSummaryDto(
                        p.getAppId(),
                        p.isPracticeDone(),
                        p.isLearnDone(),
                        p.getLastPracticeAt() == null ? null : p.getLastPracticeAt().toString(),
                        p.getLastLearnAt() == null ? null : p.getLastLearnAt().toString()
                )
        ));
    }
}