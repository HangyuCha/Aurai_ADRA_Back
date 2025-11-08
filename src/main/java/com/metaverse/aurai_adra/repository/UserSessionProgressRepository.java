package com.metaverse.aurai_adra.repository;

import com.metaverse.aurai_adra.domain.UserSessionProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionProgressRepository extends JpaRepository<UserSessionProgress, Long> {
    Optional<UserSessionProgress> findByUserIdAndAppIdAndSessionIdAndProgressType(Long userId, String appId, String sessionId, String progressType);
    List<UserSessionProgress> findByUserId(Long userId);
}