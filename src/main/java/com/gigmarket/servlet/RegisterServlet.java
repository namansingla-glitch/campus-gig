package com.gigmarket.servlet;

import com.gigmarket.dao.UserDao;
import com.gigmarket.model.User;
import com.gigmarket.util.BusinessException;
import com.gigmarket.util.Passwords;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserDao users = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (currentUser(req) != null) {
            redirect(req, resp, "/dashboard");
            return;
        }
        Map<String, String> form = new HashMap<>();
        form.put("role", User.STUDENT);
        req.setAttribute("form", form);
        render(req, resp, "register");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = param(req, "name");
        String email = param(req, "email").toLowerCase(Locale.ROOT);
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String confirm = req.getParameter("confirmPassword") == null ? "" : req.getParameter("confirmPassword");
        String role = param(req, "role");

        Map<String, String> form = new HashMap<>();
        form.put("name", name);
        form.put("email", email);
        form.put("role", User.POSTER.equals(role) ? User.POSTER : User.STUDENT);
        req.setAttribute("form", form);

        String error = validate(name, email, password, confirm, role);
        if (error != null) {
            req.setAttribute("error", error);
            render(req, resp, "register");
            return;
        }

        try {
            int id = users.create(name, email, Passwords.hash(password), role);
            User created = new User();
            created.setId(id);
            created.setName(name);
            created.setRole(role);
            signIn(req, created);
            flashSuccess(req, "Welcome to CampusGig, " + name + "!");
            redirect(req, resp, "/dashboard");
        } catch (BusinessException e) {
            req.setAttribute("error", e.getMessage());
            render(req, resp, "register");
        }
    }

    private static String validate(String name, String email, String password, String confirm, String role) {
        if (name.length() < 2 || name.length() > 100) {
            return "Please enter your full name (2-100 characters).";
        }
        if (email.length() > 190 || !EMAIL.matcher(email).matches()) {
            return "Please enter a valid email address.";
        }
        if (password.length() < Passwords.MIN_LENGTH) {
            return "Your password must be at least " + Passwords.MIN_LENGTH + " characters long.";
        }
        if (password.length() > Passwords.MAX_LENGTH) {
            return "Your password can be at most " + Passwords.MAX_LENGTH + " characters long.";
        }
        if (!password.equals(confirm)) {
            return "The two passwords do not match.";
        }
        if (!User.STUDENT.equals(role) && !User.POSTER.equals(role)) {
            return "Please choose whether you want to find work or post gigs.";
        }
        return null;
    }
}
