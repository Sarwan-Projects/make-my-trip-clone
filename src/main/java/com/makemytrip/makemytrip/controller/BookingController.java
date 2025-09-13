package com.makemytrip.makemytrip.controller;

import com.makemytrip.makemytrip.models.Users.Booking;
import com.makemytrip.makemytrip.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
