package com.makemytrip.makemytrip.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Document(collection = "seat_maps")
public class SeatMap {
    @Id
    private String id;
    private String flightId;
    private String aircraftType;
    private List<SeatRow> seatRows;
    private Map<String, Double> seatPricing; // seat type -> extra price
    
    @Getter
    @Setter
    public static class SeatRow {
        private String rowNumber;
        private List<Seat> seats;
    }
    
    @Getter
    @Setter
    public static class Seat {
        private String seatNumber;
        private String type; // "economy", "premium", "business", "first"
        private boolean available;
        private boolean isWindow;
        private boolean isAisle;
        private double extraPrice;
        private String bookedBy; // user ID if booked
    }
}