package com.makemytrip.makemytrip.controller;

import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.service.CancellationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cancellation")
@CrossOrigin(origins = "http://localhost:3000")
public class CancellationController {
    
    @Autowired
    private CancellationService cancellationService;
    
    @GetMapping("/bookings/{userId}")
    public ResponseEntity<List<Users.Booking>> getUserBookings(@PathVariable String userId) {
        try {
            List<Users.Booking> bookings = cancellationService.getUserBookings(userId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/calculate-refund")
    public ResponseEntity<Map<String, Object>> calculateRefund(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String bookingId = request.get("bookingId");
            String reason = request.get("reason");
            
            double refundAmount = cancellationService.calculateRefundAmount(userId, bookingId, reason);
            
            return ResponseEntity.ok(Map.of(
                "refundAmount", refundAmount,
                "bookingId", bookingId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/cancel")
    public ResponseEntity<Users.Booking> cancelBooking(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String bookingId = request.get("bookingId");
            String reason = request.get("reason");
            
            Users.Booking cancelledBooking = cancellationService.cancelBooking(userId, bookingId, reason);
            return ResponseEntity.ok(cancelledBooking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/refund-status")
    public ResponseEntity<Users.Booking> updateRefundStatus(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String bookingId = request.get("bookingId");
            String status = request.get("status");
            
            Users.Booking updatedBooking = cancellationService.updateRefundStatus(userId, bookingId, status);
            return ResponseEntity.ok(updatedBooking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}