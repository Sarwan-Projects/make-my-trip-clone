package com.makemytrip.makemytrip.controller;

import com.makemytrip.makemytrip.models.Recommendation;
import com.makemytrip.makemytrip.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<Recommendation>> getRecommendations(@PathVariable String userId) {
        try {
            List<Recommendation> recommendations = recommendationService.generateRecommendations(userId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{recommendationId}/feedback")
    public ResponseEntity<Map<String, String>> provideFeedback(
            @PathVariable String recommendationId,
            @RequestBody Map<String, String> feedbackData) {
        try {
            String feedback = feedbackData.get("feedback");
            recommendationService.recordRecommendationFeedback(recommendationId, feedback);
            return ResponseEntity.ok(Map.of("message", "Feedback recorded"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
