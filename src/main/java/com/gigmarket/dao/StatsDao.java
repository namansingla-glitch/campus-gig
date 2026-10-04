package com.gigmarket.dao;

import com.gigmarket.model.Gig;
import com.gigmarket.model.Stats;
import com.gigmarket.util.DataAccessException;
import com.gigmarket.util.Database;
import com.gigmarket.util.Rewards;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Read-only queries behind the dashboards and the profile page. */
public final class StatsDao {

    public Stats.Student studentStats(int userId) {
        Stats.Student s = new Stats.Student();
        String sql = "SELECT COUNT(*) AS applied, "
                + "COALESCE(SUM(a.status = 'HIRED'), 0) AS selected, "
                + "COALESCE(SUM(a.status = 'HIRED' AND g.status = 'IN_PROGRESS'), 0) AS in_progress, "
                + "COALESCE(SUM(a.status = 'HIRED' AND g.status = 'COMPLETED'), 0) AS completed, "
                + "COALESCE(SUM(CASE WHEN a.status = 'HIRED' AND g.status = 'COMPLETED' THEN g.budget END), 0) AS earnings "
                + "FROM applications a JOIN gigs g ON g.gig_id = a.gig_id WHERE a.applicant_id = ?";
        try (Connection c = Database.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        s.setApplied(rs.getInt("applied"));
                        s.setSelected(rs.getInt("selected"));
                        s.setInProgress(rs.getInt("in_progress"));
                        s.setCompleted(rs.getInt("completed"));
                        s.setEarnings(rs.getBigDecimal("earnings"));
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT AVG(rating) AS avg_rating, COUNT(*) AS cnt FROM reviews WHERE reviewee_id = ?")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt("cnt");
                        s.setReviewCount(count);
                        s.setAvgRating(count == 0 ? null : rs.getDouble("avg_rating"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        Rewards.applyTier(s);
        return s;
    }

    /** Earnings per month for the last six months (oldest first), with bar heights as percentages. */
    public List<Stats.Month> monthlyEarnings(int userId) {
        YearMonth now = YearMonth.now();
        YearMonth start = now.minusMonths(5);
        Map<String, BigDecimal> totals = new HashMap<>();

        String sql = "SELECT DATE_FORMAT(g.completed_at, '%Y-%m') AS ym, SUM(g.budget) AS total "
                + "FROM applications a JOIN gigs g ON g.gig_id = a.gig_id "
                + "WHERE a.applicant_id = ? AND a.status = 'HIRED' AND g.status = 'COMPLETED' AND g.completed_at >= ? "
                + "GROUP BY ym";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setTimestamp(2, Timestamp.valueOf(start.atDay(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    totals.put(rs.getString("ym"), rs.getBigDecimal("total"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        BigDecimal max = BigDecimal.ZERO;
        for (BigDecimal v : totals.values()) {
            if (v.compareTo(max) > 0) {
                max = v;
            }
        }
        List<Stats.Month> months = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            YearMonth ym = start.plusMonths(i);
            BigDecimal amount = totals.getOrDefault(ym.toString(), BigDecimal.ZERO);
            int percent = 0;
            if (max.signum() > 0 && amount.signum() > 0) {
                percent = Math.max(4, amount.multiply(BigDecimal.valueOf(100)).divide(max, 0, java.math.RoundingMode.HALF_UP).intValue());
            }
            months.add(new Stats.Month(ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH), amount, percent));
        }
        return months;
    }

    /** In-progress gigs the student was hired for, soonest completion deadline first. */
    public List<Gig> upcomingDeadlines(int userId, int limit) {
        String sql = "SELECT g.gig_id, g.title, g.completion_deadline "
                + "FROM applications a JOIN gigs g ON g.gig_id = a.gig_id "
                + "WHERE a.applicant_id = ? AND a.status = 'HIRED' AND g.status = 'IN_PROGRESS' "
                + "ORDER BY (g.completion_deadline IS NULL), g.completion_deadline, g.gig_id LIMIT ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            List<Gig> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Gig g = new Gig();
                    g.setId(rs.getInt("gig_id"));
                    g.setTitle(rs.getString("title"));
                    g.setCompletionDeadline(rs.getDate("completion_deadline"));
                    out.add(g);
                }
            }
            return out;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** Every student ranked by points (all-time), highest first. */
    public List<Stats.LeaderboardEntry> leaderboard() {
        String sql = "SELECT u.user_id, u.name, COUNT(g.gig_id) AS completed, COALESCE(SUM(g.budget), 0) AS earnings "
                + "FROM users u "
                + "LEFT JOIN applications a ON a.applicant_id = u.user_id AND a.status = 'HIRED' "
                + "LEFT JOIN gigs g ON g.gig_id = a.gig_id AND g.status = 'COMPLETED' "
                + "WHERE u.role = 'student' GROUP BY u.user_id, u.name";

        class Row {
            int id;
            String name;
            int completed;
            int points;
        }
        List<Row> rows = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Row r = new Row();
                r.id = rs.getInt("user_id");
                r.name = rs.getString("name");
                r.completed = rs.getInt("completed");
                r.points = Rewards.points(r.completed, rs.getBigDecimal("earnings"));
                rows.add(r);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        rows.sort(Comparator.<Row>comparingInt(r -> -r.points).thenComparing(r -> r.name.toLowerCase(Locale.ROOT)));
        List<Stats.LeaderboardEntry> out = new ArrayList<>();
        int rank = 1;
        for (Row r : rows) {
            out.add(new Stats.LeaderboardEntry(rank++, r.id, r.name, r.completed, r.points));
        }
        return out;
    }

    public Stats.Poster posterStats(int posterId) {
        Stats.Poster p = new Stats.Poster();
        try (Connection c = Database.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COUNT(*) AS total, "
                            + "COALESCE(SUM(status = 'OPEN'), 0) AS open_gigs, "
                            + "COALESCE(SUM(status = 'IN_PROGRESS'), 0) AS in_progress, "
                            + "COALESCE(SUM(status = 'COMPLETED'), 0) AS completed "
                            + "FROM gigs WHERE poster_id = ?")) {
                ps.setInt(1, posterId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        p.setTotalGigs(rs.getInt("total"));
                        p.setOpenGigs(rs.getInt("open_gigs"));
                        p.setInProgressGigs(rs.getInt("in_progress"));
                        p.setCompletedGigs(rs.getInt("completed"));
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COUNT(*) AS total, COALESCE(SUM(a.status = 'PENDING'), 0) AS pending "
                            + "FROM applications a JOIN gigs g ON g.gig_id = a.gig_id WHERE g.poster_id = ?")) {
                ps.setInt(1, posterId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        p.setTotalApplications(rs.getInt("total"));
                        p.setPendingApplications(rs.getInt("pending"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return p;
    }

    /** The poster's most-used categories with their share of all their gigs. */
    public List<Stats.CategoryShare> categoryShares(int posterId, int limit) {
        String sql = "SELECT category, COUNT(*) AS cnt FROM gigs WHERE poster_id = ? "
                + "GROUP BY category ORDER BY cnt DESC, category LIMIT ?";
        List<String> names = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        int total = 0;
        try (Connection c = Database.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM gigs WHERE poster_id = ?")) {
                ps.setInt(1, posterId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getInt(1);
                }
            }
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, posterId);
                ps.setInt(2, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        names.add(rs.getString("category"));
                        counts.add(rs.getInt("cnt"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        List<Stats.CategoryShare> out = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            out.add(new Stats.CategoryShare(names.get(i), counts.get(i), total == 0 ? 0 : counts.get(i) * 100 / total));
        }
        return out;
    }
}
