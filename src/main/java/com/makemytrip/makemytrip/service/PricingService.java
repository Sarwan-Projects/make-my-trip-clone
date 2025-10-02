package com.makemytrip.makemytrip.service;

import com.makemytrip.makemytrip.models.PriceHistory;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.repository.PriceHistoryRepository;
import com.makemytrip.makemytrip.repository.FlightRepository;
import com.makemytrip.makemytrip.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.*;

@Service
public class PricingService {
    
    @Autowired
    private PriceHistoryRepository priceHistoryRepository;
    
    @Autowired
    private FlightRepository flightRepository;
    
    @Autowired
    private HotelRepository hotelRepository;
    
    private final List<String> holidays = Arrays.asList(
        "2025-01-01", "2025-01-26", "2025-03-14", "2025-08-15", "2025-10-02", "2025-12-25"
    );
    
    public double calculateDynamicPrice(String itemId, String itemType, LocalDateTime travelDate) {
        double basePrice = getBasePrice(itemId, itemType);
        double multiplier = 1.0;
        
        // Weekend pricing
        if (travelDate.getDayOfWeek() == DayOfWeek.FRIDAY || 
            travelDate.getDayOfWeek() == DayOfWeek.SATURDAY || 
            travelDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            multiplier += 0.15; // 15% increase
        }
        
        // Holiday pricing
        String dateStr = travelDate.toLocalDate().toString();
        if (holidays.contains(dateStr)) {
            multiplier += 0.25; // 25% increase
        }
        
        // Last minute booking (within 7 days)
        long daysUntilTravel = java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), travelDate);
        if (daysUntilTravel <= 7) {
            multiplier += 0.20; // 20% increase
        } else if (daysUntilTravel <= 30) {
            multiplier += 0.10; // 10% increase
        }
        
        // Demand-based pricing (mock)
        Random random = new Random();
        double demandMultiplier = 0.8 + (random.nextDouble() * 0.4); // 0.8 to 1.2
        multiplier *= demandMultiplier;
        
        double finalPrice = basePrice * multiplier;
        
        // Update price history
        updatePriceHistory(itemId, itemType, finalPrice, basePrice, multiplier);
        
        return Math.round(finalPrice * 100.0) / 100.0;
    }
    
    private double getBasePrice(String itemId, String itemType) {
        if ("flight".equals(itemType)) {
            Optional<Flight> flight = flightRepository.findById(itemId);
            return flight.map(Flight::getPrice).orElse(500.0);
        } else if ("hotel".equals(itemType)) {
            Optional<Hotel> hotel = hotelRepository.findById(itemId);
            return hotel.map(Hotel::getPricePerNight).orElse(150.0);
        }
        return 100.0;
    }
    
    private void updatePriceHistory(String itemId, String itemType, double currentPrice, double basePrice, double multiplier) {
        Optional<PriceHistory> historyOpt = priceHistoryRepository.findByItemIdAndItemType(itemId, itemType);
        PriceHistory history = historyOpt.orElse(new PriceHistory());
        
        if (history.getId() == null) {
            history.setItemId(itemId);
            history.setItemType(itemType);
            history.setBasePrice(basePrice);
            history.setPricePoints(new ArrayList<>());
        }
        
        history.setCurrentPrice(currentPrice);
        history.setDemandMultiplier(multiplier);
        history.setLastUpdated(LocalDateTime.now());
        
        // Add new price point
        PriceHistory.PricePoint point = new PriceHistory.PricePoint();
        point.setTimestamp(LocalDateTime.now());
        point.setPrice(currentPrice);
        point.setReason(determineReason(multiplier));
        
        history.getPricePoints().add(point);
        
        // Keep only last 30 price points
        if (history.getPricePoints().size() > 30) {
            history.getPricePoints().remove(0);
        }
        
        priceHistoryRepository.save(history);
    }
    
    private String determineReason(double multiplier) {
        if (multiplier > 1.2) {
            return "high-demand";
        } else if (multiplier > 1.1) {
            return "weekend";
        } else if (multiplier > 1.0) {
            return "holiday";
        } else {
            return "normal";
        }
    }
    
    public PriceHistory getPriceHistory(String itemId, String itemType) {
        return priceHistoryRepository.findByItemIdAndItemType(itemId, itemType)
            .orElse(new PriceHistory());
    }
    
    public Map<String, Object> getPriceInsights(String itemId, String itemType) {
        PriceHistory history = getPriceHistory(itemId, itemType);
        Map<String, Object> insights = new HashMap<>();
        
        if (history.getPricePoints().isEmpty()) {
            insights.put("trend", "stable");
            insights.put("recommendation", "Good time to book");
            return insights;
        }
        
        List<PriceHistory.PricePoint> points = history.getPricePoints();
        double avgPrice = points.stream().mapToDouble(PriceHistory.PricePoint::getPrice).average().orElse(0);
        double currentPrice = history.getCurrentPrice();
        
        if (currentPrice < avgPrice * 0.9) {
            insights.put("trend", "decreasing");
            insights.put("recommendation", "Great time to book! Price is below average");
        } else if (currentPrice > avgPrice * 1.1) {
            insights.put("trend", "increasing");
            insights.put("recommendation", "Consider waiting, price is above average");
        } else {
            insights.put("trend", "stable");
            insights.put("recommendation", "Good time to book");
        }
        
        insights.put("averagePrice", Math.round(avgPrice * 100.0) / 100.0);
        insights.put("currentPrice", currentPrice);
        insights.put("priceHistory", points);
        
        return insights;
    }
    
    public boolean freezePrice(String itemId, String itemType, String userId, int hours) {
        // Mock implementation - in real scenario, you'd store this in database
        // For now, just return success
        return true;
    }
}