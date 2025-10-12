package com.metaverse.aurai_adra.service;

import com.metaverse.aurai_adra.domain.Suggestion;
import com.metaverse.aurai_adra.repository.SuggestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;

    public SuggestionService(SuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }

    // 건의사항 추가
    @Transactional
    public Suggestion addSuggestion(Suggestion suggestion) {
        return suggestionRepository.save(suggestion);
    }

    // 건의사항 목록 조회
    public List<Suggestion> getSuggestions() {
        return suggestionRepository.findAll();
    }

    // 건의사항 단일 조회
    public Optional<Suggestion> getSuggestion(Long id) {
        return suggestionRepository.findById(id);
    }

    // 건의사항 수정
    @Transactional
    public Suggestion updateSuggestion(Long id, String title, String content) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Suggestion not found with id: " + id));

        suggestion.update(title, content);
        return suggestion;
    }

    // 건의사항 삭제
    @Transactional
    public void deleteSuggestion(Long id) {
        suggestionRepository.deleteById(id);
    }
}