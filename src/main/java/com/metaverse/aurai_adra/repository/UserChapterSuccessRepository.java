package com.metaverse.aurai_adra.repository;

import com.metaverse.aurai_adra.domain.UserChapterSuccess;
import com.metaverse.aurai_adra.domain.UserChapterSuccessId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChapterSuccessRepository extends JpaRepository<UserChapterSuccess, UserChapterSuccessId> {
    List<UserChapterSuccess> findByIdUserIdOrderByIdChapterIdAsc(String userId);
    boolean existsById(UserChapterSuccessId id);
    long countByIdUserId(String userId);
}