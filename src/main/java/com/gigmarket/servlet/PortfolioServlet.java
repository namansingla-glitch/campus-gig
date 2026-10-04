package com.gigmarket.servlet;

import com.gigmarket.dao.ApplicationDao;
import com.gigmarket.model.Application;
import com.gigmarket.model.SessionUser;
import com.gigmarket.util.FileStorage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Downloads an application's portfolio. Only the student who applied and the poster who owns
 * the gig may fetch it; the file is always sent as an attachment, never rendered by the browser.
 */
@WebServlet("/portfolio")
public class PortfolioServlet extends BaseServlet {

    private final ApplicationDao applications = new ApplicationDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionUser user = currentUser(req);
        Integer id = intParam(req, "applicationId");
        Application application = id == null ? null : applications.findById(id);

        boolean allowed = application != null
                && (application.getApplicantId() == user.getId() || application.getGigPosterId() == user.getId());
        Path file = allowed ? FileStorage.resolve(application.getPortfolioFile()) : null;
        if (file == null || !Files.isRegularFile(file)) {
            notFound(resp);
            return;
        }

        String downloadName = application.getPortfolioName() == null || application.getPortfolioName().isEmpty()
                ? application.getPortfolioFile() : application.getPortfolioName();
        String asciiName = downloadName.replaceAll("[^A-Za-z0-9._-]", "_");
        String encodedName = URLEncoder.encode(downloadName, "UTF-8").replace("+", "%20");

        resp.setContentType(FileStorage.contentTypeFor(application.getPortfolioFile()));
        resp.setContentLengthLong(Files.size(file));
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + asciiName + "\"; filename*=UTF-8''" + encodedName);
        resp.setHeader("X-Content-Type-Options", "nosniff");
        try (OutputStream out = resp.getOutputStream()) {
            Files.copy(file, out);
        }
    }
}
