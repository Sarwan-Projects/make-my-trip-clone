package com.makemytrip.makemytrip.controller;

import com.makemytrip.makemytrip.models.Booking;
import com.makemytrip.makemytrip.service.CancellationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cancellation")
@CrossOrigin(origins = "*")
public class CancellationController {
    
    @Autowired
    private CancellationService cancellationService;
    
    @GetMapping("/bookings/{userId}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable String userId) {
        try {
            List<Booking> bookings = cancellationService.getUserBookings(userId);
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
    public ResponseEntity<Booking> cancelBooking(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String bookingId = request.get("bookingId");
            String reason = request.get("reason");
            
            Booking cancelledBooking = cancellationService.cancelBooking(userId, bookingId, reason);
            return ResponseEntity.ok(cancelledBooking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/refund-status")
    public ResponseEntity<Booking> updateRefundStatus(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String bookingId = request.get("bookingId");
            String status = request.get("status");
            
            Booking updatedBooking = cancellationService.updateRefundStatus(userId, bookingId, status);
            return ResponseEntity.ok(updatedBooking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}