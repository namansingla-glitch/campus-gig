package com.gigmarket.util;

import com.gigmarket.model.Stats;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/** Points, tiers and achievements. Everything is derived from real completed gigs. */
public final class Rewards {

    public static final int SILVER_AT = 500;
    public static final int GOLD_AT = 2000;

    private Rewards() {
    }

    /** 100 points per completed gig, plus 10 points per Rs 1,000 earned. */
    public static int points(int completed, BigDecimal earnings) {
        BigDecimal e = earnings == null ? BigDecimal.ZERO : earnings;
        int fromEarnings = e.multiply(BigDecimal.TEN)
                .divide(BigDecimal.valueOf(1000), 0, RoundingMode.DOWN).intValue();
        return completed * 100 + fromEarnings;
    }

    /** Fills points, tier and progress fields of the student stats object. */
    public static void applyTier(Stats.Student s) {
        int points = points(s.getCompleted(), s.getEarnings());
        s.setPoints(points);
        if (points >= GOLD_AT) {
            s.setTier("Gold");
            s.setNextTier(null);
            s.setPointsToNext(0);
            s.setProgressPercent(100);
            return;
        }
        int start;
        int next;
        if (points >= SILVER_AT) {
            s.setTier("Silver");
            s.setNextTier("Gold");
            start = SILVER_AT;
            next = GOLD_AT;
        } else {
            s.setTier("Bronze");
            s.setNextTier("Silver");
            start = 0;
            next = SILVER_AT;
        }
        s.setPointsToNext(next - points);
        s.setProgressPercent((points - start) * 100 / (next - start));
    }

    public static List<Stats.Achievement> achievements(Stats.Student s) {
        List<Stats.Achievement> list = new ArrayList<>();
        list.add(new Stats.Achievement("First Gig", "Complete your first micro-task.", s.getCompleted() >= 1));
        list.add(new Stats.Achievement("Top Earner", "Earn over ₹1,000 on CampusGig.",
                s.getEarnings().compareTo(BigDecimal.valueOf(1000)) > 0));
        list.add(new Stats.Achievement("Highly Rated", "Keep a 4.5+ rating across at least 3 reviews.",
                s.getReviewCount() >= 3 && s.getAvgRating() != null && s.getAvgRating() >= 4.5));
        list.add(new Stats.Achievement("Perfect 10", "Complete 10 gigs successfully.", s.getCompleted() >= 10));
        return list;
    }
}
