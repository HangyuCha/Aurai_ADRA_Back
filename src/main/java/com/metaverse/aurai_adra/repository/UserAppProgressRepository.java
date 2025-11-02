package com.metaverse.aurai_adra.repository;

import com.metaverse.aurai_adra.domain.UserAppProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAppProgressRepository extends JpaRepository<UserAppProgress, Long> {
    Optional<UserAppProgress> findByUserIdAndAppId(Long userId, String appId);
    List<UserAppProgress> findByUserId(Long userId);
}