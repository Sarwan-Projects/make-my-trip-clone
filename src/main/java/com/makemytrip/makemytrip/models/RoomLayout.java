package com.makemytrip.makemytrip.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Document(collection = "room_layouts")
public class RoomLayout {
    @Id
    private String id;
    private String hotelId;
    private List<RoomType> roomTypes;
    
    @Getter
    @Setter
    public static class RoomType {
        private String type; // "standard", "deluxe", "suite"
        private String description;
        private List<String> amenities;
        private double basePrice;
        private List<Room> rooms;
        private String previewImage;
        private List<String> images3D;
    }
    
    @Getter
    @Setter
    public static class Room {
        private String roomNumber;
        private String floor;
        private boolean available;
        private String view; // "city", "ocean", "garden"
        private Map<String, Object> features; // balcony, size, etc.
        private String bookedBy; // user ID if booked
    }
}