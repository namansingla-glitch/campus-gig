package com.gigmarket.dao;

import com.gigmarket.model.Review;
import com.gigmarket.util.BusinessException;
import com.gigmarket.util.DataAccessException;
import com.gigmarket.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class ReviewDao {

    private static final String BASE =
            "SELECT r.review_id, r.gig_id, r.reviewer_id, r.reviewee_id, r.rating, r.comment, r.created_at, "
            + "g.title AS gig_title, u.name AS reviewer_name "
            + "FROM reviews r JOIN gigs g ON g.gig_id = r.gig_id JOIN users u ON u.user_id = r.reviewer_id ";

    private static Review map(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getInt("review_id"));
        r.setGigId(rs.getInt("gig_id"));
        r.setGigTitle(rs.getString("gig_title"));
        r.setReviewerId(rs.getInt("reviewer_id"));
        r.setReviewerName(rs.getString("reviewer_name"));
        r.setRevieweeId(rs.getInt("reviewee_id"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }

    /** The review left for a gig, or null. */
    public Review findForGig(int gigId) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(BASE + "WHERE r.gig_id = ?")) {
            ps.setInt(1, gigId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public List<Review> findByReviewee(int userId, int limit) {
        String sql = BASE + "WHERE r.reviewee_id = ? ORDER BY r.created_at DESC, r.review_id DESC LIMIT ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            List<Review> out = new ArrayList<>();
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
     * Rates the hired student of a completed gig. One atomic statement checks that the caller
     * owns the gig and that it is completed; the unique key on gig_id allows only one review per gig.
     */
    public void create(int posterId, int gigId, int rating, String comment) {
        String sql = "INSERT INTO reviews (gig_id, reviewer_id, reviewee_id, rating, comment) "
                + "SELECT g.gig_id, g.poster_id, a.applicant_id, ?, ? "
                + "FROM gigs g JOIN applications a ON a.gig_id = g.gig_id AND a.status = 'HIRED' "
                + "WHERE g.gig_id = ? AND g.poster_id = ? AND g.status = 'COMPLETED'";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setInt(3, gigId);
            ps.setInt(4, posterId);
            if (ps.executeUpdate() == 0) {
                throw new BusinessException("You can only rate the student on a completed gig that you posted.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new BusinessException("You have already rated this gig.");
            }
            throw new DataAccessException(e);
        }
    }
}
