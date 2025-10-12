package com.metaverse.aurai_adra.controller;

import com.metaverse.aurai_adra.domain.Suggestion;
import com.metaverse.aurai_adra.service.SuggestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suggestions")
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    // 1. 건의사항 목록 조회 (GET /api/suggestions)
    @GetMapping
    public ResponseEntity<List<Suggestion>> getSuggestions() {
        List<Suggestion> suggestions = suggestionService.getSuggestions();
        return ResponseEntity.ok(suggestions);
    }

    // 2. 건의사항 추가 (POST /api/suggestions)
    @PostMapping
    public ResponseEntity<Suggestion> addSuggestion(@RequestBody Suggestion suggestion) {
        Suggestion savedSuggestion = suggestionService.addSuggestion(suggestion);
        return ResponseEntity.status(201).body(savedSuggestion);
    }

    // 3. 건의사항 단일 조회 (GET /api/suggestions/{id})
    @GetMapping("/{id}")
    public ResponseEntity<Suggestion> getSuggestion(@PathVariable Long id) {
        return suggestionService.getSuggestion(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 4. 건의사항 수정 (PUT /api/suggestions/{id})
    @PutMapping("/{id}")
    public ResponseEntity<Suggestion> updateSuggestion(@PathVariable Long id, @RequestBody Map<String, String> fields) {
        try {
            String title = fields.get("title");
            String content = fields.get("content");
            Suggestion updated = suggestionService.updateSuggestion(id, title, content);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. 건의사항 삭제 (DELETE /api/suggestions/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuggestion(@PathVariable Long id) {
        try {
            suggestionService.deleteSuggestion(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}