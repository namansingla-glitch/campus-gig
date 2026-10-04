package com.gigmarket.dao;

import com.gigmarket.model.Application;
import com.gigmarket.util.BusinessException;
import com.gigmarket.util.DataAccessException;
import com.gigmarket.util.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class ApplicationDao {

    private static final String BASE =
            "SELECT a.application_id, a.gig_id, a.applicant_id, a.pitch_text, a.portfolio_file, a.portfolio_name, "
            + "a.status, a.applied_at, "
            + "g.title AS gig_title, g.status AS gig_status, g.budget AS gig_budget, "
            + "g.completion_deadline AS gig_completion_deadline, g.poster_id AS gig_poster_id, "
            + "u.name AS applicant_name, "
            + "(SELECT AVG(r.rating) FROM reviews r WHERE r.reviewee_id = a.applicant_id) AS applicant_avg_rating, "
            + "(SELECT COUNT(*) FROM reviews r WHERE r.reviewee_id = a.applicant_id) AS applicant_review_count, "
            + "(SELECT r.rating FROM reviews r WHERE r.gig_id = a.gig_id AND r.reviewee_id = a.applicant_id) AS gig_rating "
            + "FROM applications a "
            + "JOIN gigs g ON g.gig_id = a.gig_id "
            + "JOIN users u ON u.user_id = a.applicant_id ";

    private static Application map(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setId(rs.getInt("application_id"));
        a.setGigId(rs.getInt("gig_id"));
        a.setApplicantId(rs.getInt("applicant_id"));
        a.setPitchText(rs.getString("pitch_text"));
        a.setPortfolioFile(rs.getString("portfolio_file"));
        a.setPortfolioName(rs.getString("portfolio_name"));
        a.setStatus(rs.getString("status"));
        a.setAppliedAt(rs.getTimestamp("applied_at"));
        a.setGigTitle(rs.getString("gig_title"));
        a.setGigStatus(rs.getString("gig_status"));
        a.setGigBudget(rs.getBigDecimal("gig_budget"));
        a.setGigCompletionDeadline(rs.getDate("gig_completion_deadline"));
        a.setGigPosterId(rs.getInt("gig_poster_id"));
        a.setApplicantName(rs.getString("applicant_name"));

        double avg = rs.getDouble("applicant_avg_rating");
        a.setApplicantAvgRating(rs.wasNull() ? null : avg);
        a.setApplicantReviewCount(rs.getInt("applicant_review_count"));

        int rating = rs.getInt("gig_rating");
        a.setGigRating(rs.wasNull() ? null : rating);
        return a;
    }

    public Application findById(int id) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(BASE + "WHERE a.application_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** The student's own application to a gig, or null if they have not applied. */
    public Application findForApplicant(int gigId, int applicantId) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(BASE + "WHERE a.gig_id = ? AND a.applicant_id = ?")) {
            ps.setInt(1, gigId);
            ps.setInt(2, applicantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** All applications to a gig: hired first, then pending, then rejected. */
    public List<Application> findByGig(int gigId) {
        String sql = BASE + "WHERE a.gig_id = ? ORDER BY FIELD(a.status, 'HIRED', 'PENDING', 'REJECTED'), a.applied_at ASC";
        return list(sql, gigId);
    }

    public List<Application> findByApplicant(int applicantId) {
        return list(BASE + "WHERE a.applicant_id = ? ORDER BY a.applied_at DESC", applicantId);
    }

    private List<Application> list(String sql, int id) {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            List<Application> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(map(rs));
                }
            }
            return out;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Records an application in one atomic statement that also re-checks that the gig is still open,
     * its deadline has not passed and the applicant is not the poster.
     */
    public void create(int gigId, int applicantId, String pitch, String portfolioFile, String portfolioName) {
        String sql = "INSERT INTO applications (gig_id, applicant_id, pitch_text, portfolio_file, portfolio_name) "
                + "SELECT g.gig_id, ?, ?, ?, ? FROM gigs g "
                + "WHERE g.gig_id = ? AND g.status = 'OPEN' AND g.poster_id <> ? "
                + "AND (g.application_deadline IS NULL OR g.application_deadline >= ?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, applicantId);
            ps.setString(2, pitch);
            ps.setString(3, portfolioFile);
            ps.setString(4, portfolioName);
            ps.setInt(5, gigId);
            ps.setInt(6, applicantId);
            ps.setDate(7, Date.valueOf(LocalDate.now()));
            if (ps.executeUpdate() == 0) {
                throw new BusinessException("This gig is no longer accepting applications.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new BusinessException("You have already applied to this gig.");
            }
            throw new DataAccessException(e);
        }
    }

    /**
     * Hires one applicant: marks them HIRED, rejects every other pending applicant and moves the
     * gig to IN_PROGRESS - all in one transaction with the gig row locked, so two hires cannot race.
     *
     * @return the gig id
     */
    public int hire(int posterId, int applicationId) {
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                int gigId;
                String lockSql = "SELECT g.gig_id, g.poster_id, g.status AS gig_status, a.status AS app_status "
                        + "FROM applications a JOIN gigs g ON g.gig_id = a.gig_id "
                        + "WHERE a.application_id = ? FOR UPDATE";
                try (PreparedStatement ps = c.prepareStatement(lockSql)) {
                    ps.setInt(1, applicationId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || rs.getInt("poster_id") != posterId) {
                            throw new BusinessException("Application not found.");
                        }
                        if (!"OPEN".equals(rs.getString("gig_status"))) {
                            throw new BusinessException("This gig is no longer open for hiring.");
                        }
                        if (!"PENDING".equals(rs.getString("app_status"))) {
                            throw new BusinessException("Only pending applications can be hired.");
                        }
                        gigId = rs.getInt("gig_id");
                    }
                }

                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE applications SET status = 'HIRED' WHERE application_id = ?")) {
                    ps.setInt(1, applicationId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE applications SET status = 'REJECTED' "
                                + "WHERE gig_id = ? AND application_id <> ? AND status = 'PENDING'")) {
                    ps.setInt(1, gigId);
                    ps.setInt(2, applicationId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE gigs SET status = 'IN_PROGRESS' WHERE gig_id = ?")) {
                    ps.setInt(1, gigId);
                    ps.executeUpdate();
                }
                c.commit();
                return gigId;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** Rejects one pending application on a gig that is still open. */
    public void reject(int posterId, int applicationId) {
        String sql = "UPDATE applications a JOIN gigs g ON g.gig_id = a.gig_id "
                + "SET a.status = 'REJECTED' "
                + "WHERE a.application_id = ? AND g.poster_id = ? AND a.status = 'PENDING' AND g.status = 'OPEN'";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            ps.setInt(2, posterId);
            if (ps.executeUpdate() == 0) {
                throw new BusinessException("That application can no longer be rejected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
