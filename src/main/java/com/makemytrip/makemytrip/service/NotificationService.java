package com.makemytrip.makemytrip.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public void sendFlightStatusUpdate(String userId, Map<String, Object> statusUpdate) {
        messagingTemplate.convertAndSend("/topic/flight-status/" + userId, statusUpdate);
    }
    
    public void sendPriceAlert(String userId, Map<String, Object> priceAlert) {
        messagingTemplate.convertAndSend("/topic/price-alert/" + userId, priceAlert);
    }
    
    public void sendBookingUpdate(String userId, Map<String, Object> bookingUpdate) {
        messagingTemplate.convertAndSend("/topic/booking-update/" + userId, bookingUpdate);
    }
}