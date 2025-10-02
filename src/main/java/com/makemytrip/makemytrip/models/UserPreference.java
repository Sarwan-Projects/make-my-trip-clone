package com.makemytrip.makemytrip.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Document(collection = "user_preferences")
public class UserPreference {
    @Id
    private String id;
    private String userId;
    private List<String> preferredDestinations;
    private List<String> preferredAirlines;
    private List<String> preferredHotelChains;
    private String preferredSeatType;
    private String preferredRoomType;
    private double budgetRange;
    private List<String> interests; // "beach", "mountains", "city", "adventure"
    private Map<String, Integer> searchHistory; // destination -> count
    private Map<String, Integer> bookingHistory; // category -> count
    private List<String> blacklistedItems; // items user doesn't want to see
}