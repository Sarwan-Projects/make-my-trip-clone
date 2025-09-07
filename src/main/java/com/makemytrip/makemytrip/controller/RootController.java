package com.makemytrip.makemytrip.controller;

import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.repository.FlightRepository;
import com.makemytrip.makemytrip.repository.HotelRepository;
import com.makemytrip.makemytrip.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class RootController
{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private HotelRepository hotelRepository;

    @GetMapping
    public String home()
    {
        return "Its running on port 8080";
    }

    @GetMapping("/hotel")
    public ResponseEntity<List<Hotel>> getallhotel()
    {
        List<Hotel> hotel = hotelRepository.findAll();
        return ResponseEntity.ok(hotel);
    }

    @GetMapping("/flight")
    public ResponseEntity<List<Flight>> getallflights()
    {
        List<Flight> flights = flightRepository.findAll();
        return ResponseEntity.ok(flights);
    }
}
