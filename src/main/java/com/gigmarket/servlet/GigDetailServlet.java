package com.gigmarket.servlet;

import com.gigmarket.dao.ApplicationDao;
import com.gigmarket.dao.GigDao;
import com.gigmarket.model.Gig;
import com.gigmarket.model.SessionUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/gig")
public class GigDetailServlet extends BaseServlet {

    private final GigDao gigs = new GigDao();
    private final ApplicationDao applications = new ApplicationDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer id = intParam(req, "id");
        Gig gig = id == null ? null : gigs.findById(id);
        if (gig == null) {
            notFound(resp);
            return;
        }

        SessionUser user = currentUser(req);
        req.setAttribute("gig", gig);
        req.setAttribute("isOwner", user != null && user.getId() == gig.getPosterId());
        if (user != null && user.isStudent()) {
            req.setAttribute("myApplication", applications.findForApplicant(gig.getId(), user.getId()));
        }
        render(req, resp, "gig-detail");
    }
}
