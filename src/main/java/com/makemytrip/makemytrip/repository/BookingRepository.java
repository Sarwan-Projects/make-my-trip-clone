package com.makemytrip.makemytrip.repository;

import com.makemytrip.makemytrip.models.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByUserId(String userId);
    List<Booking> findByUserIdAndStatus(String userId, String status);
}
