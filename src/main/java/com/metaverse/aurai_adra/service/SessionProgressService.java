package com.metaverse.aurai_adra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metaverse.aurai_adra.domain.UserSessionProgress;
import com.metaverse.aurai_adra.dto.AppProgressSummaryDto;
import com.metaverse.aurai_adra.dto.SessionProgressDto;
import com.metaverse.aurai_adra.repository.UserSessionProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SessionProgressService {

    private final UserSessionProgressRepository repo;
    private final ObjectMapper objectMapper;

    public SessionProgressService(UserSessionProgressRepository repo, ObjectMapper objectMapper) {
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public UserSessionProgress upsertProgress(Long userId, String appId, String sessionId, String type, LocalDateTime at, Map<String, Object> metadata) {
        final String progressType = (type == null ? "learn" : type);
        final LocalDateTime when = (at == null ? LocalDateTime.now() : at);
        Optional<UserSessionProgress> existing = repo.findByUserIdAndAppIdAndSessionIdAndProgressType(userId, appId, sessionId, progressType);
        try {
            String metaJson = null;
            if (metadata != null) {
                metaJson = objectMapper.writeValueAsString(metadata);
            }
            if (existing.isPresent()) {
                UserSessionProgress p = existing.get();
                p.setCompleted(true);
                p.setCompletedAt(when);
                if (metaJson != null) p.setMetadata(metaJson);
                return repo.save(p);
            } else {
                UserSessionProgress p = new UserSessionProgress(userId, appId, sessionId, progressType, true, when, metaJson);
                return repo.save(p);
            }
        } catch (Exception e) {
            // serialization error
            throw new RuntimeException("Failed to persist metadata", e);
        }
    }

    /**
     * Map per-app summary: appId -> AppProgressSummaryDto
     */
    @Transactional(readOnly = true)
    public Map<String, AppProgressSummaryDto> mapSummary(Long userId) {
        List<UserSessionProgress> list = repo.findByUserId(userId);
        Map<String, AppProgressSummaryDto> out = new HashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        for (UserSessionProgress p : list) {
            String app = p.getAppId();
            String sess = p.getSessionId();
            String type = p.getProgressType(); // learn/practice
            out.putIfAbsent(app, new AppProgressSummaryDto());
            AppProgressSummaryDto summary = out.get(app);

            // per-session map
            Map<String, Map<String, SessionProgressDto>> sessions = summary.getSessions();
            sessions.putIfAbsent(sess, new HashMap<>());

            // parse metadata json if present
            Object metaObj = null;
            try {
                if (p.getMetadata() != null) {
                    metaObj = objectMapper.readValue(p.getMetadata(), Object.class);
                }
            } catch (Exception ex) {
                metaObj = p.getMetadata();
            }

            String atIso = p.getCompletedAt() != null ? p.getCompletedAt().format(fmt) : null;
            SessionProgressDto sp = new SessionProgressDto(Boolean.TRUE.equals(p.getCompleted()), atIso, metaObj);

            sessions.get(sess).put(type, sp);

            // update app-level flags
            if ("practice".equalsIgnoreCase(type) && Boolean.TRUE.equals(p.getCompleted())) summary.setPracticeDone(true);
            if ("learn".equalsIgnoreCase(type) && Boolean.TRUE.equals(p.getCompleted())) summary.setLearnDone(true);
        }
        return out;
    }
}