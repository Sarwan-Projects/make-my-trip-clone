package com.makemytrip.makemytrip.repository;

import com.makemytrip.makemytrip.models.Flight;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FlightRepository extends MongoRepository<Flight, String>
{

}
