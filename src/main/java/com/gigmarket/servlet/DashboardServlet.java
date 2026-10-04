package com.gigmarket.servlet;

import com.gigmarket.dao.ApplicationDao;
import com.gigmarket.dao.GigDao;
import com.gigmarket.dao.StatsDao;
import com.gigmarket.model.SessionUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** One URL, two dashboards: what you see depends on your account type. */
@WebServlet("/dashboard")
public class DashboardServlet extends BaseServlet {

    private final StatsDao stats = new StatsDao();
    private final GigDao gigs = new GigDao();
    private final ApplicationDao applications = new ApplicationDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionUser user = currentUser(req);

        if (user.isStudent()) {
            req.setAttribute("stats", stats.studentStats(user.getId()));
            req.setAttribute("months", stats.monthlyEarnings(user.getId()));
            req.setAttribute("deadlines", stats.upcomingDeadlines(user.getId(), 5));
            List<?> recent = applications.findByApplicant(user.getId());
            req.setAttribute("recentApplications", recent.size() > 3 ? recent.subList(0, 3) : recent);
            req.setAttribute("greeting", greeting());
            render(req, resp, "student-dashboard");
        } else {
            req.setAttribute("stats", stats.posterStats(user.getId()));
            req.setAttribute("recentGigs", gigs.findByPoster(user.getId(), 5));
            req.setAttribute("categoryShares", stats.categoryShares(user.getId(), 4));
            render(req, resp, "poster-dashboard");
        }
    }

    private static String greeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) {
            return "Good morning";
        }
        return hour < 17 ? "Good afternoon" : "Good evening";
    }
}
