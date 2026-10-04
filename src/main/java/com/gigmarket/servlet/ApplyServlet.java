package com.gigmarket.servlet;

import com.gigmarket.dao.ApplicationDao;
import com.gigmarket.filter.CsrfFilter;
import com.gigmarket.model.SessionUser;
import com.gigmarket.util.BusinessException;
import com.gigmarket.util.FileStorage;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;

/**
 * Student applies to a gig with a pitch and a portfolio file.
 * The container enforces the size limits below; FileStorage validates type and contents.
 */
@WebServlet("/gig/apply")
@MultipartConfig(maxFileSize = 5L * 1024 * 1024, maxRequestSize = 6L * 1024 * 1024, fileSizeThreshold = 1024 * 1024)
public class ApplyServlet extends BaseServlet {

    private final ApplicationDao applications = new ApplicationDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part file = null;
        boolean tooLarge = false;
        try {
            file = req.getPart("portfolioFile");
        } catch (IllegalStateException e) {
            tooLarge = true; // thrown by the container when a size limit above is exceeded
        }

        // When a size limit is exceeded the container does not expose the form fields, so we cannot
        // tell which gig it was for. The page also checks the size in the browser before submitting.
        if (tooLarge) {
            flashError(req, "That file is too large (limit 5 MB). Please open the gig again and attach a smaller file.");
            redirect(req, resp, "/gigs");
            return;
        }

        // The CSRF filter cannot read a multipart body, so the token is checked here instead.
        if (!CsrfFilter.isValid(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Your session expired or the form was invalid. Please go back, reload the page and try again.");
            return;
        }

        SessionUser user = currentUser(req);
        Integer gigId = intParam(req, "gigId");
        if (gigId == null) {
            notFound(resp);
            return;
        }
        String back = "/gig?id=" + gigId;

        String pitch = param(req, "pitchText");
        if (pitch.length() < 20 || pitch.length() > 2000) {
            flashError(req, "Please write a pitch between 20 and 2000 characters.");
            redirect(req, resp, back);
            return;
        }

        FileStorage.StoredFile stored = null;
        try {
            stored = FileStorage.save(file);
            applications.create(gigId, user.getId(), pitch, stored.getStoredName(), stored.getOriginalName());
            flashSuccess(req, "Application submitted. The poster will review it soon.");
            redirect(req, resp, "/my-applications");
        } catch (BusinessException e) {
            if (stored != null) {
                FileStorage.deleteQuietly(stored.getStoredName());
            }
            flashError(req, e.getMessage());
            redirect(req, resp, back);
        } catch (RuntimeException e) {
            if (stored != null) {
                FileStorage.deleteQuietly(stored.getStoredName());
            }
            throw e;
        }
    }
}
