package com.makemytrip.makemytrip.service;

import com.makemytrip.makemytrip.models.*;
import com.makemytrip.makemytrip.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class DataInitializationService implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FlightStatusRepository flightStatusRepository;
    
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        // Only initialize flight statuses and preferences, no sample users or bookings
        
        // Create sample flight statuses
        if (flightStatusRepository.count() == 0) {
            createSampleFlightStatuses();
        }
        
        // No sample user preferences - let users create their own
    }
    
    private void createSampleUserWithBookings() {
        Optional<Users> existingUser = userRepository.findById("user123");
        if (existingUser.isPresent()) {
            return; // User already exists
        }
        
        Users user = new Users();
        user.set_id("user123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setRole("USER");
        user.setPhoneNumber("1234567890");
        
        // Create sample bookings in separate collection
        Booking flightBooking = new Booking();
        flightBooking.setBookingId("booking_flight_001");
        flightBooking.setUserId("user123");
        flightBooking.setType("flight");
        flightBooking.setItemId("flight_ai101");
        flightBooking.setBookingDate(LocalDateTime.now().minusDays(5));
        flightBooking.setTravelDate(LocalDateTime.now().plusDays(10));
        flightBooking.setQuantity(2);
        flightBooking.setOriginalPrice(500.0);
        flightBooking.setTotalPrice(450.0);
        flightBooking.setStatus("confirmed");
        
        Booking hotelBooking = new Booking();
        hotelBooking.setBookingId("booking_hotel_001");
        hotelBooking.setUserId("user123");
        hotelBooking.setType("hotel");
        hotelBooking.setItemId("hotel_taj_mumbai");
        hotelBooking.setBookingDate(LocalDateTime.now().minusDays(3));
        hotelBooking.setTravelDate(LocalDateTime.now().plusDays(12));
        hotelBooking.setQuantity(1);
        hotelBooking.setOriginalPrice(300.0);
        hotelBooking.setTotalPrice(280.0);
        hotelBooking.setStatus("confirmed");
        
        // Save bookings to separate collection
        bookingRepository.saveAll(Arrays.asList(flightBooking, hotelBooking));
        
        // Add booking IDs to user
        user.setBookingIds(Arrays.asList("booking_flight_001", "booking_hotel_001"));
        userRepository.save(user);
    }
    
    private void createSampleFlightStatuses() {
        FlightStatus status1 = new FlightStatus();
        status1.setFlightId("flight_ai101");
        status1.setFlightNumber("AI101");
        status1.setStatus("on-time");
        status1.setDelayMinutes(0);
        status1.setScheduledDeparture(LocalDateTime.now().plusDays(10).withHour(14).withMinute(30));
        status1.setScheduledArrival(LocalDateTime.now().plusDays(10).withHour(17).withMinute(45));
        status1.setGate("A12");
        status1.setTerminal("T2");
        status1.setLastUpdated(LocalDateTime.now());
        
        FlightStatus status2 = new FlightStatus();
        status2.setFlightId("flight_6e202");
        status2.setFlightNumber("6E202");
        status2.setStatus("delayed");
        status2.setDelayMinutes(45);
        status2.setDelayReason("Weather conditions");
        status2.setScheduledDeparture(LocalDateTime.now().plusDays(1).withHour(9).withMinute(15));
        status2.setScheduledArrival(LocalDateTime.now().plusDays(1).withHour(11).withMinute(30));
        status2.setEstimatedDeparture(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        status2.setEstimatedArrival(LocalDateTime.now().plusDays(1).withHour(12).withMinute(15));
        status2.setGate("B7");
        status2.setTerminal("T1");
        status2.setLastUpdated(LocalDateTime.now());
        
        flightStatusRepository.saveAll(Arrays.asList(status1, status2));
    }
    
    private void createSampleUserPreferences() {
        UserPreference preferences = new UserPreference();
        preferences.setUserId("user123");
        preferences.setPreferredDestinations(Arrays.asList("Mumbai", "Delhi", "Goa", "Bangalore"));
        preferences.setPreferredAirlines(Arrays.asList("Air India", "IndiGo", "SpiceJet"));
        preferences.setPreferredHotelChains(Arrays.asList("Taj", "Oberoi", "ITC"));
        preferences.setPreferredSeatType("window");
        preferences.setPreferredRoomType("deluxe");
        preferences.setBudgetRange(1000.0);
        preferences.setInterests(Arrays.asList("beach", "city", "business"));
        
        userPreferenceRepository.save(preferences);
    }
}