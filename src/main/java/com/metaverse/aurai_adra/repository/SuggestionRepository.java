package com.metaverse.aurai_adra.repository;

import com.metaverse.aurai_adra.domain.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    //
}