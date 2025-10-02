package com.makemytrip.makemytrip.service;

import com.makemytrip.makemytrip.models.FlightStatus;
import com.makemytrip.makemytrip.repository.FlightStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class FlightStatusService {
    
    @Autowired
    private FlightStatusRepository flightStatusRepository;
    
    private final Random random = new Random();
    private final String[] delayReasons = {
        "Weather conditions", "Air traffic control", "Technical maintenance", 
        "Crew scheduling", "Airport congestion", "Security check"
    };
    
    public FlightStatus getFlightStatus(String flightId) {
        Optional<FlightStatus> statusOpt = flightStatusRepository.findByFlightId(flightId);
        if (statusOpt.isEmpty()) {
            return generateMockStatus(flightId);
        }
        return statusOpt.get();
    }
    
    public FlightStatus getFlightStatusByNumber(String flightNumber) {
        Optional<FlightStatus> statusOpt = flightStatusRepository.findByFlightNumber(flightNumber);
        if (statusOpt.isEmpty()) {
            return generateMockStatusByNumber(flightNumber);
        }
        return statusOpt.get();
    }
    
    private FlightStatus generateMockStatus(String flightId) {
        FlightStatus status = new FlightStatus();
        status.setFlightId(flightId);
        status.setFlightNumber("AI" + (1000 + random.nextInt(9000)));
        
        // 70% on-time, 25% delayed, 5% cancelled
        int statusRand = random.nextInt(100);
        if (statusRand < 70) {
            status.setStatus("on-time");
            status.setDelayMinutes(0);
        } else if (statusRand < 95) {
            status.setStatus("delayed");
            status.setDelayMinutes(15 + random.nextInt(120)); // 15-135 minutes
            status.setDelayReason(delayReasons[random.nextInt(delayReasons.length)]);
        } else {
            status.setStatus("cancelled");
            status.setDelayReason("Operational reasons");
        }
        
        LocalDateTime now = LocalDateTime.now();
        status.setScheduledDeparture(now.plusHours(2));
        status.setScheduledArrival(now.plusHours(5));
        
        if ("delayed".equals(status.getStatus())) {
            status.setEstimatedDeparture(status.getScheduledDeparture().plusMinutes(status.getDelayMinutes()));
            status.setEstimatedArrival(status.getScheduledArrival().plusMinutes(status.getDelayMinutes()));
        }
        
        status.setGate("A" + (1 + random.nextInt(20)));
        status.setTerminal("T" + (1 + random.nextInt(3)));
        status.setLastUpdated(now);
        
        return flightStatusRepository.save(status);
    }
    
    private FlightStatus generateMockStatusByNumber(String flightNumber) {
        FlightStatus status = new FlightStatus();
        status.setFlightNumber(flightNumber);
        status.setFlightId("flight_" + flightNumber.toLowerCase());
        
        // Similar logic as above
        int statusRand = random.nextInt(100);
        if (statusRand < 70) {
            status.setStatus("on-time");
            status.setDelayMinutes(0);
        } else if (statusRand < 95) {
            status.setStatus("delayed");
            status.setDelayMinutes(15 + random.nextInt(120));
            status.setDelayReason(delayReasons[random.nextInt(delayReasons.length)]);
        } else {
            status.setStatus("cancelled");
            status.setDelayReason("Operational reasons");
        }
        
        LocalDateTime now = LocalDateTime.now();
        status.setScheduledDeparture(now.plusHours(2));
        status.setScheduledArrival(now.plusHours(5));
        
        if ("delayed".equals(status.getStatus())) {
            status.setEstimatedDeparture(status.getScheduledDeparture().plusMinutes(status.getDelayMinutes()));
            status.setEstimatedArrival(status.getScheduledArrival().plusMinutes(status.getDelayMinutes()));
        }
        
        status.setGate("A" + (1 + random.nextInt(20)));
        status.setTerminal("T" + (1 + random.nextInt(3)));
        status.setLastUpdated(now);
        
        return flightStatusRepository.save(status);
    }
    
    public List<FlightStatus> getUserFlightStatuses(List<String> flightIds) {
        return flightStatusRepository.findByFlightIdIn(flightIds);
    }
    
    public FlightStatus updateFlightStatus(String flightId, String status, int delayMinutes, String reason) {
        Optional<FlightStatus> statusOpt = flightStatusRepository.findByFlightId(flightId);
        FlightStatus flightStatus = statusOpt.orElse(new FlightStatus());
        
        flightStatus.setFlightId(flightId);
        flightStatus.setStatus(status);
        flightStatus.setDelayMinutes(delayMinutes);
        flightStatus.setDelayReason(reason);
        flightStatus.setLastUpdated(LocalDateTime.now());
        
        if (delayMinutes > 0 && flightStatus.getScheduledDeparture() != null) {
            flightStatus.setEstimatedDeparture(flightStatus.getScheduledDeparture().plusMinutes(delayMinutes));
            flightStatus.setEstimatedArrival(flightStatus.getScheduledArrival().plusMinutes(delayMinutes));
        }
        
        return flightStatusRepository.save(flightStatus);
    }
}