package com.gigmarket.util;

import org.mindrot.jbcrypt.BCrypt;

/** BCrypt helpers. Plain-text passwords are never stored or logged. */
public final class Passwords {

    /** BCrypt only looks at the first 72 bytes, so longer passwords are rejected at sign-up. */
    public static final int MAX_LENGTH = 72;
    public static final int MIN_LENGTH = 8;

    private static final int COST = 12;
    private static volatile String dummyHash;

    private Passwords() {
    }

    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(COST));
    }

    public static boolean verify(String plain, String hash) {
        if (plain == null || hash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plain, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Spend the same time as a real check. Used when the email is unknown so that response
     * time does not reveal which emails are registered.
     */
    public static void burn(String plain) {
        String h = dummyHash;
        if (h == null) {
            h = BCrypt.hashpw("not-a-real-password", BCrypt.gensalt(COST));
            dummyHash = h;
        }
        verify(plain, h);
    }
}
