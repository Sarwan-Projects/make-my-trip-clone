package com.makemytrip.makemytrip.service;

import com.makemytrip.makemytrip.models.*;
import com.makemytrip.makemytrip.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;
    
    @Autowired
    private RecommendationRepository recommendationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FlightRepository flightRepository;
    
    @Autowired
    private HotelRepository hotelRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    public List<Recommendation> generateRecommendations(String userId) {
        UserPreference preferences = getUserPreferences(userId);
        List<Booking> userBookings = getUserBookings(userId);
        
        List<Recommendation> recommendations = new ArrayList<>();
        
        // Generate flight recommendations
        recommendations.addAll(generateFlightRecommendations(userId, preferences, userBookings));
        
        // Generate hotel recommendations
        recommendations.addAll(generateHotelRecommendations(userId, preferences, userBookings));
        
        // Sort by score
        recommendations.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        // Save recommendations
        recommendationRepository.saveAll(recommendations);
        
        return recommendations.stream().limit(10).collect(Collectors.toList());
    }
    
    private UserPreference getUserPreferences(String userId) {
        try {
            return userPreferenceRepository.findByUserId(userId)
                .orElse(createDefaultPreferences(userId));
        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
            // Multiple preferences found, delete duplicates and create new one
            System.err.println("Found duplicate preferences for user " + userId + ", cleaning up...");
            userPreferenceRepository.deleteAll(
                userPreferenceRepository.findAll().stream()
                    .filter(p -> userId.equals(p.getUserId()))
                    .toList()
            );
            return createDefaultPreferences(userId);
        } catch (Exception e) {
            System.err.println("Error fetching preferences: " + e.getMessage());
            return createDefaultPreferences(userId);
        }
    }
    
    private UserPreference createDefaultPreferences(String userId) {
        UserPreference preferences = new UserPreference();
        preferences.setUserId(userId);
        preferences.setPreferredDestinations(new ArrayList<>());
        preferences.setPreferredAirlines(new ArrayList<>());
        preferences.setPreferredHotelChains(new ArrayList<>());
        preferences.setInterests(new ArrayList<>());
        preferences.setSearchHistory(new HashMap<>());
        preferences.setBookingHistory(new HashMap<>());
        preferences.setBlacklistedItems(new ArrayList<>());
        preferences.setBudgetRange(1000.0);
        return userPreferenceRepository.save(preferences);
    }
    
    private List<Booking> getUserBookings(String userId) {
        try {
            List<Booking> bookings = bookingRepository.findByUserId(userId);
            return bookings != null ? bookings : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error fetching bookings for user " + userId + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private List<Recommendation> generateFlightRecommendations(String userId, UserPreference preferences, List<Booking> userBookings) {
        List<Recommendation> recommendations = new ArrayList<>();
        try {
            List<Flight> allFlights = flightRepository.findAll();
            if (allFlights == null || allFlights.isEmpty()) {
                System.out.println("No flights found in database");
                return recommendations;
            }
            
            // Analyze user's booking patterns
            Map<String, Long> destinationCounts = userBookings.stream()
                .filter(b -> "flight".equals(b.getType()) && b.getItemId() != null)
                .collect(Collectors.groupingBy(b -> getFlightDestination(b.getItemId()), Collectors.counting()));
        
        for (Flight flight : allFlights) {
            if (preferences.getBlacklistedItems().contains(flight.get_id())) {
                continue;
            }
            
            double score = calculateFlightScore(flight, preferences, destinationCounts);
            
            if (score > 0.3) { // Lower threshold for more recommendations
                Recommendation rec = new Recommendation();
                rec.setUserId(userId);
                rec.setItemId(flight.get_id());
                rec.setItemType("flight");
                rec.setScore(score);
                rec.setReason(generateFlightReason(flight, preferences, score));
                rec.setTags(generateFlightTags(flight, preferences));
                rec.setCreatedAt(LocalDateTime.now());
                
                recommendations.add(rec);
            }
        }
            
            return recommendations;
        } catch (Exception e) {
            System.err.println("Error generating flight recommendations: " + e.getMessage());
            e.printStackTrace();
            return recommendations;
        }
    }
    
    private List<Recommendation> generateHotelRecommendations(String userId, UserPreference preferences, List<Booking> userBookings) {
        List<Recommendation> recommendations = new ArrayList<>();
        try {
            List<Hotel> allHotels = hotelRepository.findAll();
            if (allHotels == null || allHotels.isEmpty()) {
                System.out.println("No hotels found in database");
                return recommendations;
            }
            
            // Analyze user's booking patterns
            Map<String, Long> locationCounts = userBookings.stream()
                .filter(b -> "hotel".equals(b.getType()) && b.getItemId() != null)
                .collect(Collectors.groupingBy(b -> getHotelLocation(b.getItemId()), Collectors.counting()));
        
        for (Hotel hotel : allHotels) {
            if (preferences.getBlacklistedItems().contains(hotel.get_id())) {
                continue;
            }
            
            double score = calculateHotelScore(hotel, preferences, locationCounts);
            
            if (score > 0.3) { // Lower threshold for more recommendations
                Recommendation rec = new Recommendation();
                rec.setUserId(userId);
                rec.setItemId(hotel.get_id());
                rec.setItemType("hotel");
                rec.setScore(score);
                rec.setReason(generateHotelReason(hotel, preferences, score));
                rec.setTags(generateHotelTags(hotel, preferences));
                rec.setCreatedAt(LocalDateTime.now());
                
                recommendations.add(rec);
            }
            }
            
            return recommendations;
        } catch (Exception e) {
            System.err.println("Error generating hotel recommendations: " + e.getMessage());
            e.printStackTrace();
            return recommendations;
        }
    }
    
    private double calculateFlightScore(Flight flight, UserPreference preferences, Map<String, Long> destinationCounts) {
        double score = 0.5; // Base score
        
        // Price preference
        if (flight.getPrice() <= preferences.getBudgetRange()) {
            score += 0.2;
        } else {
            score -= 0.1;
        }
        
        // Destination preference
        if (preferences.getPreferredDestinations().contains(flight.getTo())) {
            score += 0.3;
        }
        
        // Previous booking pattern
        Long count = destinationCounts.get(flight.getTo());
        if (count != null && count > 0) {
            score += 0.2;
        }
        
        // Airline preference
        if (preferences.getPreferredAirlines().contains(flight.getFlightName())) {
            score += 0.2;
        }
        
        return Math.min(1.0, score);
    }
    
    private double calculateHotelScore(Hotel hotel, UserPreference preferences, Map<String, Long> locationCounts) {
        double score = 0.5; // Base score
        
        // Price preference
        if (hotel.getPricePerNight() <= preferences.getBudgetRange() / 3) { // Assuming 3-night stay
            score += 0.2;
        } else {
            score -= 0.1;
        }
        
        // Location preference
        if (preferences.getPreferredDestinations().contains(hotel.getLocation())) {
            score += 0.3;
        }
        
        // Previous booking pattern
        Long count = locationCounts.get(hotel.getLocation());
        if (count != null && count > 0) {
            score += 0.2;
        }
        
        // Interest matching
        for (String interest : preferences.getInterests()) {
            if (hotel.getAmenities() != null && hotel.getAmenities().toLowerCase().contains(interest.toLowerCase())) {
                score += 0.1;
            }
        }
        
        return Math.min(1.0, score);
    }
    
    private String generateFlightReason(Flight flight, UserPreference preferences, double score) {
        if (preferences.getPreferredDestinations().contains(flight.getTo())) {
            return "You've shown interest in " + flight.getTo() + " before!";
        } else if (flight.getPrice() <= preferences.getBudgetRange()) {
            return "Great deal within your budget!";
        } else if (score > 0.8) {
            return "Highly recommended based on your preferences";
        } else {
            return "Popular destination you might like";
        }
    }
    
    private String generateHotelReason(Hotel hotel, UserPreference preferences, double score) {
        if (preferences.getPreferredDestinations().contains(hotel.getLocation())) {
            return "Perfect for your favorite destination: " + hotel.getLocation();
        } else if (hotel.getPricePerNight() <= preferences.getBudgetRange() / 3) {
            return "Excellent value for money!";
        } else if (score > 0.8) {
            return "Matches your travel style perfectly";
        } else {
            return "Highly rated hotel you might enjoy";
        }
    }
    
    private List<String> generateFlightTags(Flight flight, UserPreference preferences) {
        List<String> tags = new ArrayList<>();
        
        if (flight.getPrice() <= preferences.getBudgetRange() * 0.7) {
            tags.add("budget-friendly");
        }
        
        if (preferences.getPreferredDestinations().contains(flight.getTo())) {
            tags.add("favorite-destination");
        }
        
        tags.add("flight");
        tags.add(flight.getTo().toLowerCase());
        
        return tags;
    }
    
    private List<String> generateHotelTags(Hotel hotel, UserPreference preferences) {
        List<String> tags = new ArrayList<>();
        
        if (hotel.getPricePerNight() <= preferences.getBudgetRange() * 0.3) {
            tags.add("budget-friendly");
        } else if (hotel.getPricePerNight() >= preferences.getBudgetRange() * 0.8) {
            tags.add("luxury");
        }
        
        if (preferences.getPreferredDestinations().contains(hotel.getLocation())) {
            tags.add("favorite-location");
        }
        
        tags.add("hotel");
        tags.add(hotel.getLocation().toLowerCase());
        
        return tags;
    }
    
    private String getFlightDestination(String flightId) {
        return flightRepository.findById(flightId)
            .map(Flight::getTo)
            .orElse("unknown");
    }
    
    private String getHotelLocation(String hotelId) {
        return hotelRepository.findById(hotelId)
            .map(Hotel::getLocation)
            .orElse("unknown");
    }
    
    public void recordRecommendationFeedback(String recommendationId, String feedback) {
        Optional<Recommendation> recOpt = recommendationRepository.findById(recommendationId);
        if (recOpt.isPresent()) {
            Recommendation rec = recOpt.get();
            rec.setFeedback(feedback);
            recommendationRepository.save(rec);
            
            // Update user preferences based on feedback
            updateUserPreferencesFromFeedback(rec.getUserId(), rec, feedback);
        }
    }
    
    private void updateUserPreferencesFromFeedback(String userId, Recommendation rec, String feedback) {
        UserPreference preferences = getUserPreferences(userId);
        
        if ("not-helpful".equals(feedback) || "irrelevant".equals(feedback)) {
            preferences.getBlacklistedItems().add(rec.getItemId());
        } else if ("helpful".equals(feedback)) {
            // Boost preferences for similar items
            if ("flight".equals(rec.getItemType())) {
                String destination = getFlightDestination(rec.getItemId());
                if (!preferences.getPreferredDestinations().contains(destination)) {
                    preferences.getPreferredDestinations().add(destination);
                }
            } else if ("hotel".equals(rec.getItemType())) {
                String location = getHotelLocation(rec.getItemId());
                if (!preferences.getPreferredDestinations().contains(location)) {
                    preferences.getPreferredDestinations().add(location);
                }
            }
        }
        
        userPreferenceRepository.save(preferences);
    }
}