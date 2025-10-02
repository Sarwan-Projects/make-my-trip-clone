package com.makemytrip.makemytrip.service;

import com.makemytrip.makemytrip.models.Review;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.repository.ReviewRepository;
import com.makemytrip.makemytrip.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Review createReview(Review review) {
        // Check if user has actually booked this item
        Optional<Users> userOpt = userRepository.findById(review.getUserId());
        boolean hasBooking = false;
        
        if (userOpt.isPresent()) {
            hasBooking = userOpt.get().getBookings().stream()
                .anyMatch(booking -> booking.getItemId() != null && booking.getItemId().equals(review.getItemId()) 
                    && booking.getType().equals(review.getItemType())
                    && "confirmed".equals(booking.getStatus()));
        }
        
        review.setVerified(hasBooking);
        review.setReviewDate(LocalDateTime.now());
        review.setHelpfulCount(0);
        review.setFlagged(false);
        
        return reviewRepository.save(review);
    }
    
    public List<Review> getReviewsByItem(String itemId, String itemType, String sortBy) {
        switch (sortBy) {
            case "helpful":
                return reviewRepository.findByItemIdAndItemTypeOrderByHelpfulCountDesc(itemId, itemType);
            case "recent":
                return reviewRepository.findByItemIdAndItemTypeOrderByReviewDateDesc(itemId, itemType);
            default:
                return reviewRepository.findByItemIdAndItemType(itemId, itemType);
        }
    }
    
    public Review markHelpful(String reviewId, String userId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            throw new RuntimeException("Review not found");
        }
        
        Review review = reviewOpt.get();
        if (review.getHelpfulUsers().contains(userId)) {
            throw new RuntimeException("Already marked as helpful");
        }
        
        review.getHelpfulUsers().add(userId);
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        
        return reviewRepository.save(review);
    }
    
    public Review flagReview(String reviewId, String reason) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            throw new RuntimeException("Review not found");
        }
        
        Review review = reviewOpt.get();
        review.setFlagged(true);
        review.setFlagReason(reason);
        
        return reviewRepository.save(review);
    }
    
    public Review addBusinessReply(String reviewId, String reply) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            throw new RuntimeException("Review not found");
        }
        
        Review review = reviewOpt.get();
        review.setReply(reply);
        review.setReplyDate(LocalDateTime.now());
        
        return reviewRepository.save(review);
    }
    
    public double getAverageRating(String itemId, String itemType) {
        List<Review> reviews = reviewRepository.findByItemIdAndItemType(itemId, itemType);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        return reviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);
    }
}