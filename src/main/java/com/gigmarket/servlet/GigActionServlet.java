package com.gigmarket.servlet;

import com.gigmarket.dao.GigDao;
import com.gigmarket.dao.ReviewDao;
import com.gigmarket.model.Gig;
import com.gigmarket.model.SessionUser;
import com.gigmarket.util.BusinessException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Mark a gig completed, or rate the student once it is. Only the owning poster may do either. */
@WebServlet("/gig/action")
public class GigActionServlet extends BaseServlet {

    private final GigDao gigs = new GigDao();
    private final ReviewDao reviews = new ReviewDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionUser user = currentUser(req);
        Integer gigId = intParam(req, "gigId");
        Gig gig = gigId == null ? null : gigs.findById(gigId);
        if (gig == null || gig.getPosterId() != user.getId()) {
            notFound(resp);
            return;
        }

        String action = param(req, "action");
        try {
            if ("complete".equals(action)) {
                gigs.complete(user.getId(), gig.getId());
                flashSuccess(req, "Gig marked as completed. You can now rate the student.");
            } else if ("rate".equals(action)) {
                Integer rating = intParam(req, "rating");
                if (rating == null || rating > 5) {
                    throw new BusinessException("Please choose a rating from 1 to 5 stars.");
                }
                String comment = param(req, "comment");
                if (comment.length() > 500) {
                    throw new BusinessException("Please keep the comment under 500 characters.");
                }
                reviews.create(user.getId(), gig.getId(), rating, comment.isEmpty() ? null : comment);
                flashSuccess(req, "Thanks! Your rating was saved.");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } catch (BusinessException e) {
            flashError(req, e.getMessage());
        }
        redirect(req, resp, "/applicants?gigId=" + gig.getId());
    }
}
