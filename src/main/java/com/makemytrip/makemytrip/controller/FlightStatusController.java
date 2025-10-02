package com.makemytrip.makemytrip.controller;

import com.makemytrip.makemytrip.models.FlightStatus;
import com.makemytrip.makemytrip.service.FlightStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flight-status")
@CrossOrigin(origins = "http://localhost:3000")
public class FlightStatusController {
    
    @Autowired
    private FlightStatusService flightStatusService;
    
    @GetMapping("/{flightId}")
    public ResponseEntity<FlightStatus> getFlightStatus(@PathVariable String flightId) {
        try {
            FlightStatus status = flightStatusService.getFlightStatus(flightId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/by-number/{flightNumber}")
    public ResponseEntity<FlightStatus> getFlightStatusByNumber(@PathVariable String flightNumber) {
        try {
            FlightStatus status = flightStatusService.getFlightStatusByNumber(flightNumber);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/user-flights")
    public ResponseEntity<List<FlightStatus>> getUserFlightStatuses(@RequestBody Map<String, List<String>> request) {
        try {
            List<String> flightIds = request.get("flightIds");
            List<FlightStatus> statuses = flightStatusService.getUserFlightStatuses(flightIds);
            return ResponseEntity.ok(statuses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{flightId}")
    public ResponseEntity<FlightStatus> updateFlightStatus(
            @PathVariable String flightId,
            @RequestBody Map<String, Object> request) {
        try {
            String status = (String) request.get("status");
            Integer delayMinutes = (Integer) request.get("delayMinutes");
            String reason = (String) request.get("reason");
            
            FlightStatus updatedStatus = flightStatusService.updateFlightStatus(
                flightId, status, delayMinutes != null ? delayMinutes : 0, reason);
            return ResponseEntity.ok(updatedStatus);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}