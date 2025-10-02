package com.makemytrip.makemytrip.repository;

import com.makemytrip.makemytrip.models.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends MongoRepository<Recommendation, String> {
    List<Recommendation> findByUserIdOrderByScoreDesc(String userId);
    List<Recommendation> findByUserIdAndItemType(String userId, String itemType);
}