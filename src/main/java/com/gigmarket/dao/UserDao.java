package com.gigmarket.dao;

import com.gigmarket.model.User;
import com.gigmarket.util.BusinessException;
import com.gigmarket.util.Database;
import com.gigmarket.util.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class UserDao {

    private static final String COLUMNS = "user_id, name, email, password_hash, role, created_at";

    private static User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("user_id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }

    /** @return the user, or null when no account uses this email. */
    public User findByEmail(String email) {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE email = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public User findById(int id) {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE user_id = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** @throws BusinessException if the email is already registered */
    public int create(String name, String email, String passwordHash, String role) {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setString(4, role);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new BusinessException("An account with this email already exists. Try logging in instead.");
            }
            throw new DataAccessException(e);
        }
    }
}
