package com.makemytrip.makemytrip.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "flight_status")
public class FlightStatus {
    @Id
    private String id;
    private String flightId;
    private String flightNumber;
    private String status; // "on-time", "delayed", "cancelled", "boarding", "departed", "arrived"
    private String delayReason;
    private int delayMinutes;
    private LocalDateTime scheduledDeparture;
    private LocalDateTime actualDeparture;
    private LocalDateTime estimatedDeparture;
    private LocalDateTime scheduledArrival;
    private LocalDateTime actualArrival;
    private LocalDateTime estimatedArrival;
    private String gate;
    private String terminal;
    private LocalDateTime lastUpdated;
}