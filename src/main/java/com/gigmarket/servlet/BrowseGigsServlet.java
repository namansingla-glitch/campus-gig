package com.gigmarket.servlet;

import com.gigmarket.dao.GigDao;
import com.gigmarket.model.Categories;
import com.gigmarket.model.Gig;
import com.gigmarket.model.Page;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;

@WebServlet("/gigs")
public class BrowseGigsServlet extends BaseServlet {

    private static final int PAGE_SIZE = 9;
    private final GigDao gigs = new GigDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = param(req, "q");
        if (q.length() > 100) {
            q = q.substring(0, 100);
        }
        String category = param(req, "category");
        if (!Categories.isValid(category)) {
            category = "";
        }

        String minBudgetText = param(req, "minBudget");
        BigDecimal minBudget = null;
        if (!minBudgetText.isEmpty()) {
            try {
                minBudget = new BigDecimal(minBudgetText);
                if (minBudget.signum() < 0 || minBudget.compareTo(new BigDecimal("99999999")) > 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                minBudget = null;
                minBudgetText = "";
                req.setAttribute("error", "The minimum budget must be a positive number.");
            }
        }

        Integer pageNumber = intParam(req, "page");
        Page<Gig> page = gigs.search(q, category, minBudget, pageNumber == null ? 1 : pageNumber, PAGE_SIZE);

        // Query string (already HTML-escaped) that the pager appends to its links.
        StringBuilder base = new StringBuilder();
        if (!q.isEmpty()) {
            base.append("q=").append(URLEncoder.encode(q, "UTF-8")).append("&amp;");
        }
        if (!category.isEmpty()) {
            base.append("category=").append(URLEncoder.encode(category, "UTF-8")).append("&amp;");
        }
        if (!minBudgetText.isEmpty()) {
            base.append("minBudget=").append(URLEncoder.encode(minBudgetText, "UTF-8")).append("&amp;");
        }

        req.setAttribute("result", page);
        req.setAttribute("q", q);
        req.setAttribute("category", category);
        req.setAttribute("minBudget", minBudgetText);
        req.setAttribute("categories", Categories.ALL);
        req.setAttribute("baseQuery", base.toString());
        render(req, resp, "gigs");
    }
}
