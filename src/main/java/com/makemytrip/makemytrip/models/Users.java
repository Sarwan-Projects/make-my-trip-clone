package com.makemytrip.makemytrip.models;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@Document(collection = "users")
public class Users {
    @Id
    private String _id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private String phoneNumber;
    private List<Booking> bookings = new ArrayList<>();;

    @Getter
    @Setter
    public static class Booking{
        private String type;
        private String bookingId;
        private String date;
        private int quantity;
        private double totalPrice;
        
        // Additional fields for enhanced functionality
        private String itemId; // flight or hotel ID
        private String travelDate;
        private double originalPrice;
        private String status = "confirmed"; // "confirmed", "cancelled", "refunded"
        private String cancellationReason;
        private String cancellationDate;
        private double refundAmount;
        private String refundStatus; // "pending", "processed", "rejected"
        private List<String> selectedSeats; // for flights
        private String selectedRoomType; // for hotels
        private String selectedRoom; // for hotels
        
        // Keep existing getters and setters for backward compatibility
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBookingId() {
            return bookingId;
        }

        public void setBookingId(String bookingId) {
            this.bookingId = bookingId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }
    }
}