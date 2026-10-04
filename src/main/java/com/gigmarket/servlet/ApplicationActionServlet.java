package com.gigmarket.servlet;

import com.gigmarket.dao.ApplicationDao;
import com.gigmarket.model.Application;
import com.gigmarket.model.SessionUser;
import com.gigmarket.util.BusinessException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Hire or reject one applicant. The poster must own the gig the application belongs to. */
@WebServlet("/application/action")
public class ApplicationActionServlet extends BaseServlet {

    private final ApplicationDao applications = new ApplicationDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionUser user = currentUser(req);
        Integer applicationId = intParam(req, "applicationId");
        Application application = applicationId == null ? null : applications.findById(applicationId);
        if (application == null || application.getGigPosterId() != user.getId()) {
            notFound(resp);
            return;
        }

        String action = param(req, "action");
        try {
            if ("hire".equals(action)) {
                applications.hire(user.getId(), application.getId());
                flashSuccess(req, application.getApplicantName() + " is hired. The other applicants were notified as not selected, and the gig is now in progress.");
            } else if ("reject".equals(action)) {
                applications.reject(user.getId(), application.getId());
                flashSuccess(req, "Application rejected.");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } catch (BusinessException e) {
            flashError(req, e.getMessage());
        }
        redirect(req, resp, "/applicants?gigId=" + application.getGigId());
    }
}
