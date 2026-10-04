package com.gigmarket.model;

import java.io.Serializable;

/** The small, safe-to-keep-in-session view of the logged-in user (no password hash). */
public final class SessionUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private final String role;

    public SessionUser(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }

    public boolean isStudent() { return User.STUDENT.equals(role); }
    public boolean isPoster() { return User.POSTER.equals(role); }
}
