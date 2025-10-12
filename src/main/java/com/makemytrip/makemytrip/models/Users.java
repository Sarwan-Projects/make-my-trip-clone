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
    
    // Store only booking IDs instead of embedded booking objects
    // Actual booking data is now in the separate Booking collection
    private List<String> bookingIds = new ArrayList<>();
}