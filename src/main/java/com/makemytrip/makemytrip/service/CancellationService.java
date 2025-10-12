package com.makemytrip.makemytrip.service;

import com.makemytrip.makemytrip.models.Booking;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.repository.BookingRepository;
import com.makemytrip.makemytrip.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class CancellationService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    public double calculateRefundAmount(String userId, String bookingId, String reason) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime travelDate = booking.getTravelDate();
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
    
    public Booking cancelBooking(String userId, String bookingId, String reason) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        
        if ("cancelled".equals(booking.getStatus())) {
            throw new RuntimeException("Booking already cancelled");
        }
        
        double refundAmount = calculateRefundAmount(userId, bookingId, reason);
        
        booking.setStatus("cancelled");
        booking.setCancellationReason(reason);
        booking.setCancellationDate(LocalDateTime.now());
        booking.setRefundAmount(refundAmount);
        booking.setRefundStatus(refundAmount > 0 ? "pending" : "not-applicable");
        
        return bookingRepository.save(booking);
    }
    
    public List<Booking> getUserBookings(String userId) {
        return bookingRepository.findByUserId(userId);
    }
    
    public Booking updateRefundStatus(String userId, String bookingId, String status) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        booking.setRefundStatus(status);
        return bookingRepository.save(booking);
    }
}