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

    @Transactional
    public UserAppProgress upsertAppProgress(Long userId, String appId, String type, LocalDateTime at) {
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

        return repo.save(p);
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