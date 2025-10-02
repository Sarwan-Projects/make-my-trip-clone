package com.makemytrip.makemytrip.repository;

import com.makemytrip.makemytrip.models.PriceHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceHistoryRepository extends MongoRepository<PriceHistory, String> {
    Optional<PriceHistory> findByItemIdAndItemType(String itemId, String itemType);
    List<PriceHistory> findByItemType(String itemType);
}