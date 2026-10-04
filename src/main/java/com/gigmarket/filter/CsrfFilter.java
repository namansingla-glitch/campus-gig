package com.gigmarket.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Every session gets a random token; every state-changing request (anything but GET/HEAD/OPTIONS)
 * must send it back in the "_csrf" form field. The multipart upload servlet checks the token itself
 * because a multipart body can only be parsed once the servlet is running.
 */
public class CsrfFilter implements Filter {

    public static final String SESSION_KEY = "csrfToken";
    public static final String PARAM = "_csrf";

    private static final String SELF_CHECKED_PATH = "/gig/apply";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String newToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(64);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /** True if the request carries the token that belongs to its session. */
    public static boolean isValid(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        }
        Object expected = session.getAttribute(SESSION_KEY);
        String actual = req.getParameter(PARAM);
        if (!(expected instanceof String) || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(((String) expected).getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(true);
        if (session.getAttribute(SESSION_KEY) == null) {
            session.setAttribute(SESSION_KEY, newToken());
        }

        String method = req.getMethod();
        boolean safe = "GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method);
        if (!safe && !SELF_CHECKED_PATH.equals(req.getServletPath()) && !isValid(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Your session expired or the form was invalid. Please go back, reload the page and try again.");
            return;
        }
        chain.doFilter(request, response);
    }
}
