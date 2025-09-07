package com.makemytrip.makemytrip.service;

import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.models.Users.Booking;
import com.makemytrip.makemytrip.repository.FlightRepository;
import com.makemytrip.makemytrip.repository.HotelRepository;
import com.makemytrip.makemytrip.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class BookingService
{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private HotelRepository hotelRepository;

    public Booking bookFlight(String userId, String flightId, int seats, double price)
    {
        Optional<Users> usersOptional = userRepository.findById(userId);
        Optional<Flight> flightOptional = flightRepository.findById(flightId);
        if(usersOptional.isPresent() && flightOptional.isPresent())
        {
            Users users = usersOptional.get();
            Flight flight = flightOptional.get();
            if(flight.getAvailableSeats() >= seats)
            {
                flight.setAvailableSeats(flight.getAvailableSeats() - seats);
                flightRepository.save(flight);

                Booking booking = new Booking();
                booking.setType("Flight");
                booking.setBookingId(flightId);
                booking.setDate(LocalDate.now().toString());
                booking.setQuantity(seats);
                booking.setTotalPrice(price);
                users.getBookings().add(booking);
                userRepository.save(users);
                return booking;
            }
            else
            {
                throw new RuntimeException("Not Enough Seats Available");
            }
        }
        throw new RuntimeException("User or Flight not found");
    }

    public Booking bookHotel(String userId, String hotelId, int rooms, double price)
    {
        Optional<Users> usersOptional = userRepository.findById(userId);
        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        if(usersOptional.isPresent() && hotelOptional.isPresent())
        {
            Users users = usersOptional.get();
            Hotel hotel = hotelOptional.get();
            if(hotel.getAvailableRooms() >= rooms)
            {
                hotel.setAvailableRooms(hotel.getAvailableRooms() - rooms);
                hotelRepository.save(hotel);

                Booking booking = new Booking();
                booking.setType("Hotel");
                booking.setBookingId(hotelId);
                booking.setDate(LocalDate.now().toString());
                booking.setQuantity(rooms);
                booking.setTotalPrice(price);
                users.getBookings().add(booking);
                userRepository.save(users);
                return booking;
            }
            else
            {
                throw new RuntimeException("Not Enough Rooms Available");
            }
        }
        throw new RuntimeException("User or Flight not found");
    }
}
