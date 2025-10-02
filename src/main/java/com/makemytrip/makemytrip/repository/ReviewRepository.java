package com.makemytrip.makemytrip.repository;

import com.makemytrip.makemytrip.models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByItemIdAndItemType(String itemId, String itemType);
    List<Review> findByUserId(String userId);
    List<Review> findByItemIdAndItemTypeOrderByHelpfulCountDesc(String itemId, String itemType);
    List<Review> findByItemIdAndItemTypeOrderByReviewDateDesc(String itemId, String itemType);
    List<Review> findByFlagged(boolean flagged);
    
    @Query("{ 'itemId': ?0, 'itemType': ?1, 'rating': { $gte: ?2 } }")
    List<Review> findByItemAndMinRating(String itemId, String itemType, int minRating);
}