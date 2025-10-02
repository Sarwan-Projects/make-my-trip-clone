package com.makemytrip.makemytrip.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String userId;
    private String userName;
    private String itemId; // flight or hotel ID
    private String itemType; // "flight" or "hotel"
    private int rating; // 1-5 stars
    private String title;
    private String comment;
    private List<String> photos;
    private LocalDateTime reviewDate;
    private int helpfulCount;
    private List<String> helpfulUsers;
    private boolean flagged;
    private String flagReason;
    private boolean verified; // verified booking
    private String reply; // business reply
    private LocalDateTime replyDate;
}