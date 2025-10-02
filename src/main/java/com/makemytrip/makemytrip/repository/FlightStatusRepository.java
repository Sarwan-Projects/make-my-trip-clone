package com.makemytrip.makemytrip.repository;

import com.makemytrip.makemytrip.models.FlightStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightStatusRepository extends MongoRepository<FlightStatus, String> {
    Optional<FlightStatus> findByFlightId(String flightId);
    Optional<FlightStatus> findByFlightNumber(String flightNumber);
    List<FlightStatus> findByStatus(String status);
    List<FlightStatus> findByFlightIdIn(List<String> flightIds);
}