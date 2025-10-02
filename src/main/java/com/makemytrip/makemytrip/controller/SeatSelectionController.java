package com.makemytrip.makemytrip.controller;

import com.makemytrip.makemytrip.models.SeatMap;
import com.makemytrip.makemytrip.service.SeatSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seat-selection")
@CrossOrigin(origins = "http://localhost:3000")
public class SeatSelectionController {
    
    @Autowired
    private SeatSelectionService seatSelectionService;
    
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<SeatMap> getFlightSeatMap(@PathVariable String flightId) {
        try {
            SeatMap seatMap = seatSelectionService.getFlightSeatMap(flightId);
            return ResponseEntity.ok(seatMap);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/book-seats")
    public ResponseEntity<SeatMap> bookSeats(@RequestBody Map<String, Object> request) {
        try {
            String flightId = (String) request.get("flightId");
            @SuppressWarnings("unchecked")
            List<String> seatNumbers = (List<String>) request.get("seatNumbers");
            String userId = (String) request.get("userId");
            
            SeatMap updatedSeatMap = seatSelectionService.bookSeats(flightId, seatNumbers, userId);
            return ResponseEntity.ok(updatedSeatMap);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/calculate-upgrade-price")
    public ResponseEntity<Map<String, Double>> calculateSeatUpgradePrice(@RequestBody Map<String, Object> request) {
        try {
            String flightId = (String) request.get("flightId");
            @SuppressWarnings("unchecked")
            List<String> seatNumbers = (List<String>) request.get("seatNumbers");
            
            double totalPrice = seatSelectionService.calculateSeatUpgradePrice(flightId, seatNumbers);
            return ResponseEntity.ok(Map.of("upgradePrice", totalPrice));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}