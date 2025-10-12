package com.makemytrip.makemytrip.controller;

import com.makemytrip.makemytrip.models.Booking;
import com.makemytrip.makemytrip.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController
{
    @Autowired
    private BookingService bookingService;

    @PostMapping("/hotel")
    public Booking bookHotel(@RequestParam String userId,@RequestParam String hotelId,@RequestParam int rooms,@RequestParam double price)
    {
       return bookingService.bookHotel(userId, hotelId, rooms, price);
    }

    @PostMapping("/flight")
    public Booking bookFlight(@RequestParam String userId,@RequestParam String flightId,@RequestParam int seats,@RequestParam double price)
    {
        return bookingService.bookFlight(userId, flightId, seats, price);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable String userId)
    {
        try {
            List<Booking> bookings = bookingService.getUserBookings(userId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
