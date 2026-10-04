package com.gigmarket.servlet;

import com.gigmarket.dao.UserDao;
import com.gigmarket.model.SessionUser;
import com.gigmarket.model.User;
import com.gigmarket.util.LoginThrottle;
import com.gigmarket.util.Passwords;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private final UserDao users = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionUser current = currentUser(req);
        if (current != null) {
            redirect(req, resp, "/dashboard");
            return;
        }
        req.setAttribute("next", safeNext(req.getParameter("next")));
        render(req, resp, "login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = param(req, "email").toLowerCase(Locale.ROOT);
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String next = safeNext(req.getParameter("next"));

        req.setAttribute("email", email);
        req.setAttribute("next", next);

        if (email.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Please enter your email and password.");
            render(req, resp, "login");
            return;
        }

        String throttleKey = email + "|" + req.getRemoteAddr();
        if (LoginThrottle.isBlocked(throttleKey)) {
            req.setAttribute("error", "Too many failed attempts. Please wait a few minutes and try again.");
            render(req, resp, "login");
            return;
        }

        User user = users.findByEmail(email);
        if (user == null) {
            Passwords.burn(password); // keep timing similar whether or not the email exists
        }
        if (user == null || !Passwords.verify(password, user.getPasswordHash())) {
            LoginThrottle.recordFailure(throttleKey);
            req.setAttribute("error", "Incorrect email or password.");
            render(req, resp, "login");
            return;
        }

        LoginThrottle.reset(throttleKey);
        signIn(req, user);
        redirect(req, resp, next != null ? next : "/dashboard");
    }
}
