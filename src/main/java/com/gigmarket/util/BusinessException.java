package com.gigmarket.util;

/** A rule was broken in a way the user can understand. The message is safe to show on screen. */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessException(String userMessage) {
        super(userMessage);
    }
}
