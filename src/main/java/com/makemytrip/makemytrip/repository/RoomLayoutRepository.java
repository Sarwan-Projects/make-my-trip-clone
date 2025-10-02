package com.makemytrip.makemytrip.repository;

import com.makemytrip.makemytrip.models.RoomLayout;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomLayoutRepository extends MongoRepository<RoomLayout, String> {
    Optional<RoomLayout> findByHotelId(String hotelId);
}