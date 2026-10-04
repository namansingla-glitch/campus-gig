package com.gigmarket.servlet;

import com.gigmarket.dao.GigDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/my-gigs")
public class MyGigsServlet extends BaseServlet {

    private final GigDao gigs = new GigDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("gigs", gigs.findByPoster(currentUser(req).getId(), 0));
        render(req, resp, "my-gigs");
    }
}
