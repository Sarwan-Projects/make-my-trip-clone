package com.makemytrip.makemytrip.controller;

import com.makemytrip.makemytrip.models.RoomLayout;
import com.makemytrip.makemytrip.service.RoomSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/room-selection")
@CrossOrigin(origins = "http://localhost:3000")
public class RoomSelectionController {
    
    @Autowired
    private RoomSelectionService roomSelectionService;
    
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<RoomLayout> getHotelRoomLayout(@PathVariable String hotelId) {
        try {
            RoomLayout roomLayout = roomSelectionService.getHotelRoomLayout(hotelId);
            return ResponseEntity.ok(roomLayout);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/book-room")
    public ResponseEntity<RoomLayout> bookRoom(@RequestBody Map<String, String> request) {
        try {
            String hotelId = request.get("hotelId");
            String roomNumber = request.get("roomNumber");
            String userId = request.get("userId");
            
            RoomLayout updatedLayout = roomSelectionService.bookRoom(hotelId, roomNumber, userId);
            return ResponseEntity.ok(updatedLayout);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/hotel/{hotelId}/available/{roomType}")
    public ResponseEntity<List<RoomLayout.Room>> getAvailableRoomsByType(
            @PathVariable String hotelId,
            @PathVariable String roomType) {
        try {
            List<RoomLayout.Room> availableRooms = roomSelectionService.getAvailableRoomsByType(hotelId, roomType);
            return ResponseEntity.ok(availableRooms);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}