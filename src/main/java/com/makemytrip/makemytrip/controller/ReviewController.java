package com.makemytrip.makemytrip.controller;

import com.makemytrip.makemytrip.models.Review;
import com.makemytrip.makemytrip.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        try {
            Review createdReview = reviewService.createReview(review);
            return ResponseEntity.ok(createdReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{itemId}/{itemType}")
    public ResponseEntity<List<Review>> getReviews(
            @PathVariable String itemId,
            @PathVariable String itemType,
            @RequestParam(defaultValue = "recent") String sortBy) {
        try {
            List<Review> reviews = reviewService.getReviewsByItem(itemId, itemType, sortBy);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<Review> markHelpful(@PathVariable String reviewId, @RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            Review updatedReview = reviewService.markHelpful(reviewId, userId);
            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{reviewId}/flag")
    public ResponseEntity<Review> flagReview(@PathVariable String reviewId, @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            Review flaggedReview = reviewService.flagReview(reviewId, reason);
            return ResponseEntity.ok(flaggedReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{reviewId}/reply")
    public ResponseEntity<Review> addBusinessReply(@PathVariable String reviewId, @RequestBody Map<String, String> request) {
        try {
            String reply = request.get("reply");
            Review updatedReview = reviewService.addBusinessReply(reviewId, reply);
            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{itemId}/{itemType}/average-rating")
    public ResponseEntity<Map<String, Double>> getAverageRating(@PathVariable String itemId, @PathVariable String itemType) {
        try {
            double averageRating = reviewService.getAverageRating(itemId, itemType);
            return ResponseEntity.ok(Map.of("averageRating", averageRating));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}