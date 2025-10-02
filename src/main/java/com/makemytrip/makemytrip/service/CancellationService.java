package com.makemytrip.makemytrip.service;

import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class CancellationService {
    
    @Autowired
    private UserRepository userRepository;
    
    public double calculateRefundAmount(String userId, String bookingId, String reason) {
        Users.Booking booking = findBookingByUserAndId(userId, bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking not found");
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime travelDate = LocalDateTime.parse(booking.getTravelDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        long hoursUntilTravel = ChronoUnit.HOURS.between(now, travelDate);
        
        double refundPercentage = 0.0;
        
        // Refund policy based on cancellation time
        if (hoursUntilTravel >= 48) {
            refundPercentage = 0.9; // 90% refund
        } else if (hoursUntilTravel >= 24) {
            refundPercentage = 0.5; // 50% refund
        } else if (hoursUntilTravel >= 2) {
            refundPercentage = 0.25; // 25% refund
        } else {
            refundPercentage = 0.0; // No refund
        }
        
        // Emergency reasons get better refund
        if ("medical".equals(reason) || "emergency".equals(reason)) {
            refundPercentage = Math.max(refundPercentage, 0.8);
        }
        
        return booking.getTotalPrice() * refundPercentage;
    }
    
    public Users.Booking cancelBooking(String userId, String bookingId, String reason) {
        Optional<Users> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        Users user = userOpt.get();
        Users.Booking booking = findBookingByUserAndId(userId, bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking not found");
        }
        
        if ("cancelled".equals(booking.getStatus())) {
            throw new RuntimeException("Booking already cancelled");
        }
        
        double refundAmount = calculateRefundAmount(userId, bookingId, reason);
        
        booking.setStatus("cancelled");
        booking.setCancellationReason(reason);
        booking.setCancellationDate(LocalDateTime.now().toString());
        booking.setRefundAmount(refundAmount);
        booking.setRefundStatus(refundAmount > 0 ? "pending" : "not-applicable");
        
        userRepository.save(user);
        return booking;
    }
    
    public List<Users.Booking> getUserBookings(String userId) {
        Optional<Users> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        return userOpt.get().getBookings();
    }
    
    public Users.Booking updateRefundStatus(String userId, String bookingId, String status) {
        Optional<Users> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        Users user = userOpt.get();
        Users.Booking booking = findBookingByUserAndId(userId, bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking not found");
        }
        
        booking.setRefundStatus(status);
        userRepository.save(user);
        return booking;
    }
    
    private Users.Booking findBookingByUserAndId(String userId, String bookingId) {
        Optional<Users> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return null;
        }
        
        return userOpt.get().getBookings().stream()
            .filter(booking -> bookingId.equals(booking.getBookingId()))
            .findFirst()
            .orElse(null);
    }
}