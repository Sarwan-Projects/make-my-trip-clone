package com.makemytrip.makemytrip.service;

import com.makemytrip.makemytrip.models.Booking;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.repository.BookingRepository;
import com.makemytrip.makemytrip.repository.FlightRepository;
import com.makemytrip.makemytrip.repository.HotelRepository;
import com.makemytrip.makemytrip.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
    @Autowired
    private BookingRepository bookingRepository;

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
                booking.setUserId(userId);
                booking.setType("flight");
                booking.setItemId(flightId);
                booking.setBookingDate(LocalDateTime.now());
                booking.setTravelDate(LocalDateTime.now().plusDays(1)); // Default to tomorrow
                booking.setQuantity(seats);
                booking.setOriginalPrice(price);
                booking.setTotalPrice(price);
                booking.setStatus("confirmed");
                
                // Save to separate bookings collection
                Booking savedBooking = bookingRepository.save(booking);
                
                // Add booking ID to user's booking list
                users.getBookingIds().add(savedBooking.getBookingId());
                userRepository.save(users);
                
                return savedBooking;
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
                booking.setUserId(userId);
                booking.setType("hotel");
                booking.setItemId(hotelId);
                booking.setBookingDate(LocalDateTime.now());
                booking.setTravelDate(LocalDateTime.now().plusDays(7)); // Default to next week
                booking.setQuantity(rooms);
                booking.setOriginalPrice(price);
                booking.setTotalPrice(price);
                booking.setStatus("confirmed");
                
                // Save to separate bookings collection
                Booking savedBooking = bookingRepository.save(booking);
                
                // Add booking ID to user's booking list
                users.getBookingIds().add(savedBooking.getBookingId());
                userRepository.save(users);
                
                return savedBooking;
            }
            else
            {
                throw new RuntimeException("Not Enough Rooms Available");
            }
        }
        throw new RuntimeException("User or Flight not found");
    }
    
    public List<Booking> getUserBookings(String userId) {
        return bookingRepository.findByUserId(userId);
    }
    
    public Optional<Booking> getBookingById(String bookingId) {
        return bookingRepository.findById(bookingId);
    }
    
    public Booking updateBooking(Booking booking) {
        return bookingRepository.save(booking);
    }
}
