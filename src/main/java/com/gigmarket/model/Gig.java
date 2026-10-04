package com.gigmarket.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Gig {
    public static final String OPEN = "OPEN";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String COMPLETED = "COMPLETED";

    private int id;
    private int posterId;
    private String posterName;
    private String title;
    private String description;
    private BigDecimal budget;
    private String category;
    private Date applicationDeadline;
    private Date completionDeadline;
    private String status;
    private Timestamp createdAt;
    private Timestamp completedAt;
    private int applicationCount;
    private int pendingCount;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPosterId() { return posterId; }
    public void setPosterId(int posterId) { this.posterId = posterId; }

    public String getPosterName() { return posterName; }
    public void setPosterName(String posterName) { this.posterName = posterName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Date getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(Date applicationDeadline) { this.applicationDeadline = applicationDeadline; }

    public Date getCompletionDeadline() { return completionDeadline; }
    public void setCompletionDeadline(Date completionDeadline) { this.completionDeadline = completionDeadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getCompletedAt() { return completedAt; }
    public void setCompletedAt(Timestamp completedAt) { this.completedAt = completedAt; }

    public int getApplicationCount() { return applicationCount; }
    public void setApplicationCount(int applicationCount) { this.applicationCount = applicationCount; }

    public int getPendingCount() { return pendingCount; }
    public void setPendingCount(int pendingCount) { this.pendingCount = pendingCount; }

    public boolean isOpen() { return OPEN.equals(status); }
    public boolean isInProgress() { return IN_PROGRESS.equals(status); }
    public boolean isCompleted() { return COMPLETED.equals(status); }

    /** True while the gig is open and its application deadline (if any) has not passed. */
    public boolean isAcceptingApplications() {
        return isOpen() && (applicationDeadline == null || !applicationDeadline.toLocalDate().isBefore(LocalDate.now()));
    }

    /** "3 days left", "Closes today", "Closed" or null when there is no deadline. */
    public String getDeadlineNote() {
        if (applicationDeadline == null) {
            return null;
        }
        long days = ChronoUnit.DAYS.between(LocalDate.now(), applicationDeadline.toLocalDate());
        if (days < 0) {
            return "Applications closed";
        }
        if (days == 0) {
            return "Closes today";
        }
        return days == 1 ? "1 day left" : days + " days left";
    }
}
