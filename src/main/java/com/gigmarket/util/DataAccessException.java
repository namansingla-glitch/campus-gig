package com.gigmarket.util;

/** Unexpected database failure. Results in the generic 500 page and is logged. */
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(Throwable cause) {
        super("Database error", cause);
    }
}
