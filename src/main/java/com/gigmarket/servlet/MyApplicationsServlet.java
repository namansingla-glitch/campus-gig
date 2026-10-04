package com.gigmarket.servlet;

import com.gigmarket.dao.ApplicationDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/my-applications")
public class MyApplicationsServlet extends BaseServlet {

    private final ApplicationDao applications = new ApplicationDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("applications", applications.findByApplicant(currentUser(req).getId()));
        render(req, resp, "my-applications");
    }
}
