package com.makemytrip.makemytrip.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bookings")
public class Booking {
    @Id
    private String bookingId;
    private String userId;
    private String type; // "flight" or "hotel"
    private String itemId; // flightId or hotelId
    private LocalDateTime bookingDate;
    private LocalDateTime travelDate;
    private int quantity;
    private double originalPrice;
    private double totalPrice;
    private String status; // "confirmed", "cancelled", "completed"
    private String cancellationReason;
    private double refundAmount;
    private String refundStatus; // "pending", "processed", "rejected"
    private LocalDateTime cancellationDate;
}
