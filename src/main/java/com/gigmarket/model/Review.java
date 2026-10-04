package com.gigmarket.model;

import java.sql.Timestamp;

public class Review {
    private int id;
    private int gigId;
    private String gigTitle;
    private int reviewerId;
    private String reviewerName;
    private int revieweeId;
    private int rating;
    private String comment;
    private Timestamp createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGigId() { return gigId; }
    public void setGigId(int gigId) { this.gigId = gigId; }

    public String getGigTitle() { return gigTitle; }
    public void setGigTitle(String gigTitle) { this.gigTitle = gigTitle; }

    public int getReviewerId() { return reviewerId; }
    public void setReviewerId(int reviewerId) { this.reviewerId = reviewerId; }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public int getRevieweeId() { return revieweeId; }
    public void setRevieweeId(int revieweeId) { this.revieweeId = revieweeId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
