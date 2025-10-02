package com.makemytrip.makemytrip.service;

import com.makemytrip.makemytrip.models.RoomLayout;
import com.makemytrip.makemytrip.repository.RoomLayoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoomSelectionService {
    
    @Autowired
    private RoomLayoutRepository roomLayoutRepository;
    
    public RoomLayout getHotelRoomLayout(String hotelId) {
        Optional<RoomLayout> layoutOpt = roomLayoutRepository.findByHotelId(hotelId);
        if (layoutOpt.isEmpty()) {
            return generateDefaultRoomLayout(hotelId);
        }
        return layoutOpt.get();
    }
    
    private RoomLayout generateDefaultRoomLayout(String hotelId) {
        RoomLayout layout = new RoomLayout();
        layout.setHotelId(hotelId);
        
        List<RoomLayout.RoomType> roomTypes = new ArrayList<>();
        
        // Standard rooms
        RoomLayout.RoomType standard = new RoomLayout.RoomType();
        standard.setType("standard");
        standard.setDescription("Comfortable standard room with city view");
        standard.setAmenities(Arrays.asList("WiFi", "AC", "TV", "Mini Bar"));
        standard.setBasePrice(100.0);
        standard.setPreviewImage("/images/standard-room.jpg");
        standard.setImages3D(Arrays.asList("/images/3d/standard-1.jpg", "/images/3d/standard-2.jpg"));
        
        List<RoomLayout.Room> standardRooms = new ArrayList<>();
        for (int floor = 1; floor <= 5; floor++) {
            for (int room = 1; room <= 10; room++) {
                RoomLayout.Room r = new RoomLayout.Room();
                r.setRoomNumber(floor + String.format("%02d", room));
                r.setFloor(String.valueOf(floor));
                r.setAvailable(new Random().nextBoolean());
                r.setView(floor <= 2 ? "city" : "garden");
                
                Map<String, Object> features = new HashMap<>();
                features.put("size", "25 sqm");
                features.put("balcony", floor > 2);
                r.setFeatures(features);
                
                standardRooms.add(r);
            }
        }
        standard.setRooms(standardRooms);
        roomTypes.add(standard);
        
        // Deluxe rooms
        RoomLayout.RoomType deluxe = new RoomLayout.RoomType();
        deluxe.setType("deluxe");
        deluxe.setDescription("Spacious deluxe room with premium amenities");
        deluxe.setAmenities(Arrays.asList("WiFi", "AC", "Smart TV", "Mini Bar", "Coffee Machine", "Balcony"));
        deluxe.setBasePrice(200.0);
        deluxe.setPreviewImage("/images/deluxe-room.jpg");
        deluxe.setImages3D(Arrays.asList("/images/3d/deluxe-1.jpg", "/images/3d/deluxe-2.jpg"));
        
        List<RoomLayout.Room> deluxeRooms = new ArrayList<>();
        for (int floor = 3; floor <= 8; floor++) {
            for (int room = 1; room <= 6; room++) {
                RoomLayout.Room r = new RoomLayout.Room();
                r.setRoomNumber(floor + String.format("%02d", room));
                r.setFloor(String.valueOf(floor));
                r.setAvailable(new Random().nextBoolean());
                r.setView(floor >= 6 ? "ocean" : "garden");
                
                Map<String, Object> features = new HashMap<>();
                features.put("size", "35 sqm");
                features.put("balcony", true);
                features.put("bathtub", true);
                r.setFeatures(features);
                
                deluxeRooms.add(r);
            }
        }
        deluxe.setRooms(deluxeRooms);
        roomTypes.add(deluxe);
        
        // Suite rooms
        RoomLayout.RoomType suite = new RoomLayout.RoomType();
        suite.setType("suite");
        suite.setDescription("Luxury suite with separate living area and ocean view");
        suite.setAmenities(Arrays.asList("WiFi", "AC", "Smart TV", "Mini Bar", "Coffee Machine", "Balcony", "Jacuzzi", "Butler Service"));
        suite.setBasePrice(500.0);
        suite.setPreviewImage("/images/suite-room.jpg");
        suite.setImages3D(Arrays.asList("/images/3d/suite-1.jpg", "/images/3d/suite-2.jpg", "/images/3d/suite-3.jpg"));
        
        List<RoomLayout.Room> suiteRooms = new ArrayList<>();
        for (int floor = 8; floor <= 10; floor++) {
            for (int room = 1; room <= 4; room++) {
                RoomLayout.Room r = new RoomLayout.Room();
                r.setRoomNumber(floor + String.format("%02d", room));
                r.setFloor(String.valueOf(floor));
                r.setAvailable(new Random().nextBoolean());
                r.setView("ocean");
                
                Map<String, Object> features = new HashMap<>();
                features.put("size", "60 sqm");
                features.put("balcony", true);
                features.put("bathtub", true);
                features.put("livingArea", true);
                features.put("kitchenette", true);
                r.setFeatures(features);
                
                suiteRooms.add(r);
            }
        }
        suite.setRooms(suiteRooms);
        roomTypes.add(suite);
        
        layout.setRoomTypes(roomTypes);
        return roomLayoutRepository.save(layout);
    }
    
    public RoomLayout bookRoom(String hotelId, String roomNumber, String userId) {
        RoomLayout layout = getHotelRoomLayout(hotelId);
        
        for (RoomLayout.RoomType roomType : layout.getRoomTypes()) {
            for (RoomLayout.Room room : roomType.getRooms()) {
                if (room.getRoomNumber().equals(roomNumber)) {
                    if (!room.isAvailable()) {
                        throw new RuntimeException("Room " + roomNumber + " is not available");
                    }
                    room.setAvailable(false);
                    room.setBookedBy(userId);
                    return roomLayoutRepository.save(layout);
                }
            }
        }
        
        throw new RuntimeException("Room not found");
    }
    
    public List<RoomLayout.Room> getAvailableRoomsByType(String hotelId, String roomType) {
        RoomLayout layout = getHotelRoomLayout(hotelId);
        
        return layout.getRoomTypes().stream()
            .filter(rt -> rt.getType().equals(roomType))
            .findFirst()
            .map(rt -> rt.getRooms().stream()
                .filter(RoomLayout.Room::isAvailable)
                .toList())
            .orElse(new ArrayList<>());
    }
}