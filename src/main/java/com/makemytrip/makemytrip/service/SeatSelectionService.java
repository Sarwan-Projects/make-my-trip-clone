package com.makemytrip.makemytrip.service;

import com.makemytrip.makemytrip.models.SeatMap;
import com.makemytrip.makemytrip.repository.SeatMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SeatSelectionService {
    
    @Autowired
    private SeatMapRepository seatMapRepository;
    
    public SeatMap getFlightSeatMap(String flightId) {
        Optional<SeatMap> seatMapOpt = seatMapRepository.findByFlightId(flightId);
        if (seatMapOpt.isEmpty()) {
            return generateDefaultSeatMap(flightId);
        }
        return seatMapOpt.get();
    }
    
    private SeatMap generateDefaultSeatMap(String flightId) {
        SeatMap seatMap = new SeatMap();
        seatMap.setFlightId(flightId);
        seatMap.setAircraftType("Boeing 737");
        
        // Pricing for different seat types
        Map<String, Double> pricing = new HashMap<>();
        pricing.put("economy", 0.0);
        pricing.put("premium", 50.0);
        pricing.put("business", 200.0);
        seatMap.setSeatPricing(pricing);
        
        List<SeatMap.SeatRow> rows = new ArrayList<>();
        
        // Business class (rows 1-3)
        for (int row = 1; row <= 3; row++) {
            SeatMap.SeatRow seatRow = new SeatMap.SeatRow();
            seatRow.setRowNumber(String.valueOf(row));
            
            List<SeatMap.Seat> seats = new ArrayList<>();
            String[] seatLetters = {"A", "B", "C", "D"};
            
            for (String letter : seatLetters) {
                SeatMap.Seat seat = new SeatMap.Seat();
                seat.setSeatNumber(row + letter);
                seat.setType("business");
                seat.setAvailable(new Random().nextBoolean());
                seat.setWindow(letter.equals("A") || letter.equals("D"));
                seat.setAisle(letter.equals("B") || letter.equals("C"));
                seat.setExtraPrice(200.0);
                seats.add(seat);
            }
            
            seatRow.setSeats(seats);
            rows.add(seatRow);
        }
        
        // Premium economy (rows 4-6)
        for (int row = 4; row <= 6; row++) {
            SeatMap.SeatRow seatRow = new SeatMap.SeatRow();
            seatRow.setRowNumber(String.valueOf(row));
            
            List<SeatMap.Seat> seats = new ArrayList<>();
            String[] seatLetters = {"A", "B", "C", "D", "E", "F"};
            
            for (String letter : seatLetters) {
                SeatMap.Seat seat = new SeatMap.Seat();
                seat.setSeatNumber(row + letter);
                seat.setType("premium");
                seat.setAvailable(new Random().nextBoolean());
                seat.setWindow(letter.equals("A") || letter.equals("F"));
                seat.setAisle(letter.equals("C") || letter.equals("D"));
                seat.setExtraPrice(50.0);
                seats.add(seat);
            }
            
            seatRow.setSeats(seats);
            rows.add(seatRow);
        }
        
        // Economy class (rows 7-30)
        for (int row = 7; row <= 30; row++) {
            SeatMap.SeatRow seatRow = new SeatMap.SeatRow();
            seatRow.setRowNumber(String.valueOf(row));
            
            List<SeatMap.Seat> seats = new ArrayList<>();
            String[] seatLetters = {"A", "B", "C", "D", "E", "F"};
            
            for (String letter : seatLetters) {
                SeatMap.Seat seat = new SeatMap.Seat();
                seat.setSeatNumber(row + letter);
                seat.setType("economy");
                seat.setAvailable(new Random().nextBoolean());
                seat.setWindow(letter.equals("A") || letter.equals("F"));
                seat.setAisle(letter.equals("C") || letter.equals("D"));
                seat.setExtraPrice(0.0);
                seats.add(seat);
            }
            
            seatRow.setSeats(seats);
            rows.add(seatRow);
        }
        
        seatMap.setSeatRows(rows);
        return seatMapRepository.save(seatMap);
    }
    
    public SeatMap bookSeats(String flightId, List<String> seatNumbers, String userId) {
        SeatMap seatMap = getFlightSeatMap(flightId);
        
        for (SeatMap.SeatRow row : seatMap.getSeatRows()) {
            for (SeatMap.Seat seat : row.getSeats()) {
                if (seatNumbers.contains(seat.getSeatNumber())) {
                    if (!seat.isAvailable()) {
                        throw new RuntimeException("Seat " + seat.getSeatNumber() + " is not available");
                    }
                    seat.setAvailable(false);
                    seat.setBookedBy(userId);
                }
            }
        }
        
        return seatMapRepository.save(seatMap);
    }
    
    public double calculateSeatUpgradePrice(String flightId, List<String> seatNumbers) {
        SeatMap seatMap = getFlightSeatMap(flightId);
        double totalPrice = 0.0;
        
        for (SeatMap.SeatRow row : seatMap.getSeatRows()) {
            for (SeatMap.Seat seat : row.getSeats()) {
                if (seatNumbers.contains(seat.getSeatNumber())) {
                    totalPrice += seat.getExtraPrice();
                }
            }
        }
        
        return totalPrice;
    }
}