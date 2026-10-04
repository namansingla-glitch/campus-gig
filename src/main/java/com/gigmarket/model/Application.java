package com.gigmarket.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class Application {
    public static final String PENDING = "PENDING";
    public static final String HIRED = "HIRED";
    public static final String REJECTED = "REJECTED";

    private int id;
    private int gigId;
    private String gigTitle;
    private String gigStatus;
    private BigDecimal gigBudget;
    private Date gigCompletionDeadline;
    private int gigPosterId;
    private int applicantId;
    private String applicantName;
    private Double applicantAvgRating;
    private int applicantReviewCount;
    private String pitchText;
    private String portfolioFile;
    private String portfolioName;
    private String status;
    private Timestamp appliedAt;
    private Integer gigRating;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGigId() { return gigId; }
    public void setGigId(int gigId) { this.gigId = gigId; }

    public String getGigTitle() { return gigTitle; }
    public void setGigTitle(String gigTitle) { this.gigTitle = gigTitle; }

    public String getGigStatus() { return gigStatus; }
    public void setGigStatus(String gigStatus) { this.gigStatus = gigStatus; }

    public BigDecimal getGigBudget() { return gigBudget; }
    public void setGigBudget(BigDecimal gigBudget) { this.gigBudget = gigBudget; }

    public Date getGigCompletionDeadline() { return gigCompletionDeadline; }
    public void setGigCompletionDeadline(Date gigCompletionDeadline) { this.gigCompletionDeadline = gigCompletionDeadline; }

    public int getGigPosterId() { return gigPosterId; }
    public void setGigPosterId(int gigPosterId) { this.gigPosterId = gigPosterId; }

    public int getApplicantId() { return applicantId; }
    public void setApplicantId(int applicantId) { this.applicantId = applicantId; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public Double getApplicantAvgRating() { return applicantAvgRating; }
    public void setApplicantAvgRating(Double applicantAvgRating) { this.applicantAvgRating = applicantAvgRating; }

    public int getApplicantReviewCount() { return applicantReviewCount; }
    public void setApplicantReviewCount(int applicantReviewCount) { this.applicantReviewCount = applicantReviewCount; }

    public String getPitchText() { return pitchText; }
    public void setPitchText(String pitchText) { this.pitchText = pitchText; }

    public String getPortfolioFile() { return portfolioFile; }
    public void setPortfolioFile(String portfolioFile) { this.portfolioFile = portfolioFile; }

    public String getPortfolioName() { return portfolioName; }
    public void setPortfolioName(String portfolioName) { this.portfolioName = portfolioName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getAppliedAt() { return appliedAt; }
    public void setAppliedAt(Timestamp appliedAt) { this.appliedAt = appliedAt; }

    /** Rating the poster gave this student for this gig, or null. */
    public Integer getGigRating() { return gigRating; }
    public void setGigRating(Integer gigRating) { this.gigRating = gigRating; }

    public boolean isPending() { return PENDING.equals(status); }
    public boolean isHired() { return HIRED.equals(status); }
    public boolean isRejected() { return REJECTED.equals(status); }
    public boolean isHasPortfolio() { return portfolioFile != null && !portfolioFile.isEmpty(); }

    /** Where the application is in its life: UNDER_REVIEW, NOT_SELECTED, IN_PROGRESS or COMPLETED. */
    public String getStage() {
        if (isRejected()) {
            return "NOT_SELECTED";
        }
        if (isHired()) {
            return Gig.COMPLETED.equals(gigStatus) ? "COMPLETED" : "IN_PROGRESS";
        }
        return "UNDER_REVIEW";
    }
}
