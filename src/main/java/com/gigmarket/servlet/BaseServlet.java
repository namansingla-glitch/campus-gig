package com.gigmarket.servlet;

import com.gigmarket.filter.CsrfFilter;
import com.gigmarket.model.SessionUser;
import com.gigmarket.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Pattern;

/** Small helpers shared by all servlets. */
public abstract class BaseServlet extends HttpServlet {

    private static final Pattern SAFE_NEXT = Pattern.compile("^/[A-Za-z0-9/_\\-.?=&%]*$");

    protected static SessionUser currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (SessionUser) session.getAttribute("user");
    }

    /** Forwards to /WEB-INF/views/&lt;view&gt;.jsp - JSPs are never reachable directly. */
    protected void render(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    protected static void flashSuccess(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashSuccess", message);
    }

    protected static void flashError(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashError", message);
    }

    /** Trimmed parameter value; never null. */
    protected static String param(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }

    /** Positive integer parameter, or null if missing or not a valid number. */
    protected static Integer intParam(HttpServletRequest req, String name) {
        try {
            int value = Integer.parseInt(param(req, name));
            return value > 0 ? value : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Only same-site relative paths are allowed as a post-login destination (no open redirects). */
    protected static String safeNext(String next) {
        if (next == null || next.length() > 200 || next.startsWith("//") || !SAFE_NEXT.matcher(next).matches()) {
            return null;
        }
        return next;
    }

    /** Starts a fresh authenticated session (new session id, new CSRF token). */
    protected static void signIn(HttpServletRequest req, User user) {
        req.getSession(true);
        req.changeSessionId();
        HttpSession session = req.getSession();
        session.setAttribute("user", new SessionUser(user.getId(), user.getName(), user.getRole()));
        session.setAttribute(CsrfFilter.SESSION_KEY, CsrfFilter.newToken());
        session.setMaxInactiveInterval(30 * 60);
    }

    protected static void notFound(HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
