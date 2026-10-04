package com.gigmarket.servlet;

import com.gigmarket.dao.GigDao;
import com.gigmarket.model.Gig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"", "/home"})
public class HomeServlet extends BaseServlet {

    private static final Logger LOG = Logger.getLogger(HomeServlet.class.getName());
    private final GigDao gigs = new GigDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Gig> latest = Collections.emptyList();
        try {
            latest = gigs.search(null, null, null, 1, 3).getItems();
        } catch (RuntimeException e) {
            // The landing page should still load if the database is down.
            LOG.log(Level.WARNING, "Could not load latest gigs for the home page", e);
        }
        req.setAttribute("latestGigs", latest);
        render(req, resp, "home");
    }
}
