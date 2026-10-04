package com.gigmarket.dao;

import com.gigmarket.model.Gig;
import com.gigmarket.model.Page;
import com.gigmarket.util.BusinessException;
import com.gigmarket.util.DataAccessException;
import com.gigmarket.util.Database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class GigDao {

    private static final String BASE =
            "SELECT g.*, u.name AS poster_name, "
            + "(SELECT COUNT(*) FROM applications a WHERE a.gig_id = g.gig_id) AS app_count, "
            + "(SELECT COUNT(*) FROM applications a WHERE a.gig_id = g.gig_id AND a.status = 'PENDING') AS pending_count "
            + "FROM gigs g JOIN users u ON u.user_id = g.poster_id ";

    private static Gig map(ResultSet rs) throws SQLException {
        Gig g = new Gig();
        g.setId(rs.getInt("gig_id"));
        g.setPosterId(rs.getInt("poster_id"));
        g.setPosterName(rs.getString("poster_name"));
        g.setTitle(rs.getString("title"));
        g.setDescription(rs.getString("description"));
        g.setBudget(rs.getBigDecimal("budget"));
        g.setCategory(rs.getString("category"));
        g.setApplicationDeadline(rs.getDate("application_deadline"));
        g.setCompletionDeadline(rs.getDate("completion_deadline"));
        g.setStatus(rs.getString("status"));
        g.setCreatedAt(rs.getTimestamp("created_at"));
        g.setCompletedAt(rs.getTimestamp("completed_at"));
        g.setApplicationCount(rs.getInt("app_count"));
        g.setPendingCount(rs.getInt("pending_count"));
        return g;
    }

    public int create(int posterId, String title, String description, BigDecimal budget, String category,
                      LocalDate applicationDeadline, LocalDate completionDeadline) {
        String sql = "INSERT INTO gigs (poster_id, title, description, budget, category, "
                + "application_deadline, completion_deadline) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, posterId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setBigDecimal(4, budget);
            ps.setString(5, category);
            ps.setDate(6, applicationDeadline == null ? null : Date.valueOf(applicationDeadline));
            ps.setDate(7, completionDeadline == null ? null : Date.valueOf(completionDeadline));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public boolean existsOpenWithTitle(int posterId, String title) {
        String sql = "SELECT 1 FROM gigs WHERE poster_id = ? AND LOWER(title) = LOWER(?) AND status = 'OPEN' LIMIT 1";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, posterId);
            ps.setString(2, title);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** @return the gig, or null if it does not exist. */
    public Gig findById(int id) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(BASE + "WHERE g.gig_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** Open gigs that can still take applications, newest first. */
    public Page<Gig> search(String q, String category, BigDecimal minBudget, int page, int pageSize) {
        StringBuilder where = new StringBuilder(
                "WHERE g.status = 'OPEN' AND (g.application_deadline IS NULL OR g.application_deadline >= ?)");
        List<Object> params = new ArrayList<>();
        params.add(Date.valueOf(LocalDate.now()));

        if (q != null && !q.trim().isEmpty()) {
            String like = "%" + escapeLike(q.trim()) + "%";
            where.append(" AND (g.title LIKE ? OR g.description LIKE ?)");
            params.add(like);
            params.add(like);
        }
        if (category != null && !category.isEmpty()) {
            where.append(" AND g.category = ?");
            params.add(category);
        }
        if (minBudget != null) {
            where.append(" AND g.budget >= ?");
            params.add(minBudget);
        }

        try (Connection c = Database.getConnection()) {
            int total;
            try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM gigs g " + where)) {
                bind(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getInt(1);
                }
            }

            int pages = Math.max(1, (total + pageSize - 1) / pageSize);
            int safePage = Math.min(Math.max(1, page), pages);

            List<Object> listParams = new ArrayList<>(params);
            listParams.add(pageSize);
            listParams.add((safePage - 1) * pageSize);
            List<Gig> gigs = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(
                    BASE + where + " ORDER BY g.created_at DESC, g.gig_id DESC LIMIT ? OFFSET ?")) {
                bind(ps, listParams);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gigs.add(map(rs));
                    }
                }
            }
            return new Page<>(gigs, safePage, pageSize, total);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** A poster's own gigs, newest first. limit &lt;= 0 means "all". */
    public List<Gig> findByPoster(int posterId, int limit) {
        String sql = BASE + "WHERE g.poster_id = ? ORDER BY g.created_at DESC, g.gig_id DESC"
                + (limit > 0 ? " LIMIT ?" : "");
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, posterId);
            if (limit > 0) {
                ps.setInt(2, limit);
            }
            List<Gig> gigs = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    gigs.add(map(rs));
                }
            }
            return gigs;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** Moves a gig from IN_PROGRESS to COMPLETED. Only the poster who owns it can do this. */
    public void complete(int posterId, int gigId) {
        String sql = "UPDATE gigs SET status = 'COMPLETED', completed_at = CURRENT_TIMESTAMP "
                + "WHERE gig_id = ? AND poster_id = ? AND status = 'IN_PROGRESS'";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, gigId);
            ps.setInt(2, posterId);
            if (ps.executeUpdate() == 0) {
                throw new BusinessException("Only a gig that is in progress can be marked as completed.");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private static void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
