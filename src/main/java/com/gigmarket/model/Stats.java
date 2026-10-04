package com.gigmarket.model;

import java.math.BigDecimal;

/** Plain holders for dashboard and profile numbers. All values come from the database. */
public final class Stats {

    private Stats() {
    }

    public static class Student {
        private int applied;
        private int selected;
        private int inProgress;
        private int completed;
        private BigDecimal earnings = BigDecimal.ZERO;
        private Double avgRating;
        private int reviewCount;
        private int points;
        private String tier = "Bronze";
        private String nextTier;
        private int pointsToNext;
        private int progressPercent;

        public int getApplied() { return applied; }
        public void setApplied(int applied) { this.applied = applied; }
        public int getSelected() { return selected; }
        public void setSelected(int selected) { this.selected = selected; }
        public int getInProgress() { return inProgress; }
        public void setInProgress(int inProgress) { this.inProgress = inProgress; }
        public int getCompleted() { return completed; }
        public void setCompleted(int completed) { this.completed = completed; }
        public BigDecimal getEarnings() { return earnings; }
        public void setEarnings(BigDecimal earnings) { this.earnings = earnings == null ? BigDecimal.ZERO : earnings; }
        public Double getAvgRating() { return avgRating; }
        public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
        public int getReviewCount() { return reviewCount; }
        public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
        public int getPoints() { return points; }
        public void setPoints(int points) { this.points = points; }
        public String getTier() { return tier; }
        public void setTier(String tier) { this.tier = tier; }
        public String getNextTier() { return nextTier; }
        public void setNextTier(String nextTier) { this.nextTier = nextTier; }
        public int getPointsToNext() { return pointsToNext; }
        public void setPointsToNext(int pointsToNext) { this.pointsToNext = pointsToNext; }
        public int getProgressPercent() { return progressPercent; }
        public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }
    }

    public static class Poster {
        private int totalGigs;
        private int openGigs;
        private int inProgressGigs;
        private int completedGigs;
        private int totalApplications;
        private int pendingApplications;

        public int getTotalGigs() { return totalGigs; }
        public void setTotalGigs(int totalGigs) { this.totalGigs = totalGigs; }
        public int getOpenGigs() { return openGigs; }
        public void setOpenGigs(int openGigs) { this.openGigs = openGigs; }
        public int getInProgressGigs() { return inProgressGigs; }
        public void setInProgressGigs(int inProgressGigs) { this.inProgressGigs = inProgressGigs; }
        public int getCompletedGigs() { return completedGigs; }
        public void setCompletedGigs(int completedGigs) { this.completedGigs = completedGigs; }
        public int getTotalApplications() { return totalApplications; }
        public void setTotalApplications(int totalApplications) { this.totalApplications = totalApplications; }
        public int getPendingApplications() { return pendingApplications; }
        public void setPendingApplications(int pendingApplications) { this.pendingApplications = pendingApplications; }

        /** Average applications per gig, one decimal, or null when there are no gigs yet. */
        public Double getAvgApplicationsPerGig() {
            if (totalGigs == 0) {
                return null;
            }
            return Math.round(totalApplications * 10.0 / totalGigs) / 10.0;
        }
    }

    public static class LeaderboardEntry {
        private final int rank;
        private final int userId;
        private final String name;
        private final int completed;
        private final int points;

        public LeaderboardEntry(int rank, int userId, String name, int completed, int points) {
            this.rank = rank;
            this.userId = userId;
            this.name = name;
            this.completed = completed;
            this.points = points;
        }

        public int getRank() { return rank; }
        public int getUserId() { return userId; }
        public String getName() { return name; }
        public int getCompleted() { return completed; }
        public int getPoints() { return points; }
    }

    public static class Month {
        private final String label;
        private final BigDecimal amount;
        private final int percent;

        public Month(String label, BigDecimal amount, int percent) {
            this.label = label;
            this.amount = amount;
            this.percent = percent;
        }

        public String getLabel() { return label; }
        public BigDecimal getAmount() { return amount; }
        public int getPercent() { return percent; }
    }

    public static class CategoryShare {
        private final String category;
        private final int count;
        private final int percent;

        public CategoryShare(String category, int count, int percent) {
            this.category = category;
            this.count = count;
            this.percent = percent;
        }

        public String getCategory() { return category; }
        public int getCount() { return count; }
        public int getPercent() { return percent; }
    }

    public static class Achievement {
        private final String title;
        private final String description;
        private final boolean unlocked;

        public Achievement(String title, String description, boolean unlocked) {
            this.title = title;
            this.description = description;
            this.unlocked = unlocked;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public boolean isUnlocked() { return unlocked; }
    }
}
