package com.gigmarket.servlet;

import com.gigmarket.dao.GigDao;
import com.gigmarket.model.Categories;
import com.gigmarket.model.SessionUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/post-gig")
public class PostGigServlet extends BaseServlet {

    private static final BigDecimal MAX_BUDGET = new BigDecimal("1000000");

    private final GigDao gigs = new GigDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> form = new HashMap<>();
        form.put("category", "Development");
        showForm(req, resp, form);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionUser user = currentUser(req);

        String title = param(req, "title");
        String description = param(req, "description");
        String budgetText = param(req, "budget");
        String category = param(req, "category");
        boolean hasTimeline = "true".equals(param(req, "hasTimeline"));
        String deadlineText = hasTimeline ? param(req, "deadline") : "";
        String completionText = hasTimeline ? param(req, "completionDeadline") : "";

        Map<String, String> form = new HashMap<>();
        form.put("title", title);
        form.put("description", description);
        form.put("budget", budgetText);
        form.put("category", category);
        form.put("hasTimeline", hasTimeline ? "true" : "");
        form.put("deadline", deadlineText);
        form.put("completionDeadline", completionText);

        if (title.length() < 3 || title.length() > 200) {
            fail(req, resp, form, "The title must be between 3 and 200 characters.");
            return;
        }
        if (description.length() < 10 || description.length() > 5000) {
            fail(req, resp, form, "The description must be between 10 and 5000 characters.");
            return;
        }
        if (!Categories.isValid(category)) {
            fail(req, resp, form, "Please choose one of the listed categories.");
            return;
        }

        BigDecimal budget;
        try {
            budget = new BigDecimal(budgetText).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            fail(req, resp, form, "Please enter the budget as a number.");
            return;
        }
        if (budget.compareTo(BigDecimal.ONE) < 0 || budget.compareTo(MAX_BUDGET) > 0) {
            fail(req, resp, form, "The budget must be between ₹1 and ₹10,00,000.");
            return;
        }

        LocalDate applicationDeadline = null;
        LocalDate completionDeadline = null;
        if (hasTimeline) {
            try {
                applicationDeadline = LocalDate.parse(deadlineText);
                completionDeadline = LocalDate.parse(completionText);
            } catch (DateTimeParseException e) {
                fail(req, resp, form, "Please pick both dates for the timeline.");
                return;
            }
            if (applicationDeadline.isBefore(LocalDate.now())) {
                fail(req, resp, form, "The application deadline cannot be in the past.");
                return;
            }
            if (!completionDeadline.isAfter(applicationDeadline)) {
                fail(req, resp, form, "The completion deadline must be after the application deadline.");
                return;
            }
        }

        if (gigs.existsOpenWithTitle(user.getId(), title)) {
            fail(req, resp, form, "You already have an open gig with this title.");
            return;
        }

        int id = gigs.create(user.getId(), title, description, budget, category, applicationDeadline, completionDeadline);
        flashSuccess(req, "Your gig is live.");
        redirect(req, resp, "/gig?id=" + id);
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, Map<String, String> form, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        showForm(req, resp, form);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Map<String, String> form)
            throws ServletException, IOException {
        req.setAttribute("form", form);
        req.setAttribute("categories", Categories.ALL);
        req.setAttribute("today", LocalDate.now().toString());
        render(req, resp, "post-gig");
    }
}
