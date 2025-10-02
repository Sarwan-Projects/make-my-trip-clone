package com.makemytrip.makemytrip.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "recommendations")
public class Recommendation {
    @Id
    private String id;
    private String userId;
    private String itemId;
    private String itemType; // "flight" or "hotel"
    private double score;
    private String reason;
    private List<String> tags;
    private LocalDateTime createdAt;
    private boolean clicked;
    private boolean booked;
    private String feedback; // "helpful", "not-helpful", "irrelevant"
}