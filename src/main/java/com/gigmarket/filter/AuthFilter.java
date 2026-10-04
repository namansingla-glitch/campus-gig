package com.gigmarket.filter;

import com.gigmarket.model.SessionUser;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * One place that decides who may open what. Pages not listed here (home, login, register,
 * browse, gig details) are public. Servlets additionally check ownership of the records they touch.
 */
public class AuthFilter implements Filter {

    private static final String ANY_USER = "*";
    private static final Map<String, String> RULES = new HashMap<>();

    static {
        RULES.put("/dashboard", ANY_USER);
        RULES.put("/logout", ANY_USER);
        RULES.put("/portfolio", ANY_USER);

        RULES.put("/my-applications", "student");
        RULES.put("/profile", "student");
        RULES.put("/gig/apply", "student");

        RULES.put("/post-gig", "poster");
        RULES.put("/my-gigs", "poster");
        RULES.put("/applicants", "poster");
        RULES.put("/application/action", "poster");
        RULES.put("/gig/action", "poster");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String requiredRole = RULES.get(req.getServletPath());
        if (requiredRole == null) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        SessionUser user = session == null ? null : (SessionUser) session.getAttribute("user");

        if (user == null) {
            String target = req.getContextPath() + "/login";
            if ("GET".equals(req.getMethod())) {
                String next = req.getServletPath() + (req.getQueryString() == null ? "" : "?" + req.getQueryString());
                target += "?next=" + URLEncoder.encode(next, "UTF-8");
            }
            resp.sendRedirect(target);
            return;
        }

        if (!ANY_USER.equals(requiredRole) && !requiredRole.equals(user.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "This page is only available to " + requiredRole + " accounts.");
            return;
        }
        chain.doFilter(request, response);
    }
}
