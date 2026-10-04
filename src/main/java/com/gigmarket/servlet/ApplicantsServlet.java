package com.gigmarket.servlet;

import com.gigmarket.dao.ApplicationDao;
import com.gigmarket.dao.GigDao;
import com.gigmarket.dao.ReviewDao;
import com.gigmarket.model.Application;
import com.gigmarket.model.Gig;
import com.gigmarket.model.SessionUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** Lets a poster review the applicants of one of their own gigs. */
@WebServlet("/applicants")
public class ApplicantsServlet extends BaseServlet {

    private final GigDao gigs = new GigDao();
    private final ApplicationDao applications = new ApplicationDao();
    private final ReviewDao reviews = new ReviewDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionUser user = currentUser(req);
        Integer gigId = intParam(req, "gigId");
        Gig gig = gigId == null ? null : gigs.findById(gigId);
        if (gig == null || gig.getPosterId() != user.getId()) {
            notFound(resp); // same answer for "missing" and "not yours", so ids cannot be probed
            return;
        }

        List<Application> list = applications.findByGig(gig.getId());
        Application hired = null;
        for (Application a : list) {
            if (a.isHired()) {
                hired = a;
                break;
            }
        }

        req.setAttribute("gig", gig);
        req.setAttribute("applications", list);
        req.setAttribute("hired", hired);
        req.setAttribute("review", reviews.findForGig(gig.getId()));
        render(req, resp, "applicants");
    }
}
