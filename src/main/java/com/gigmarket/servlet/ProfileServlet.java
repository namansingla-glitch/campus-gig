package com.gigmarket.servlet;

import com.gigmarket.dao.ReviewDao;
import com.gigmarket.dao.StatsDao;
import com.gigmarket.dao.UserDao;
import com.gigmarket.model.SessionUser;
import com.gigmarket.model.Stats;
import com.gigmarket.model.User;
import com.gigmarket.util.Rewards;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/profile")
public class ProfileServlet extends BaseServlet {

    private final UserDao users = new UserDao();
    private final StatsDao stats = new StatsDao();
    private final ReviewDao reviews = new ReviewDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionUser sessionUser = currentUser(req);
        User account = users.findById(sessionUser.getId());
        if (account == null) {
            notFound(resp);
            return;
        }

        Stats.Student studentStats = stats.studentStats(account.getId());
        List<Stats.Achievement> achievements = Rewards.achievements(studentStats);
        long unlocked = achievements.stream().filter(Stats.Achievement::isUnlocked).count();

        List<Stats.LeaderboardEntry> board = stats.leaderboard();
        int myRank = 0;
        for (Stats.LeaderboardEntry entry : board) {
            if (entry.getUserId() == account.getId()) {
                myRank = entry.getRank();
                break;
            }
        }

        req.setAttribute("account", account);
        req.setAttribute("stats", studentStats);
        req.setAttribute("achievements", achievements);
        req.setAttribute("unlockedCount", unlocked);
        req.setAttribute("leaderboard", board.size() > 5 ? board.subList(0, 5) : board);
        req.setAttribute("myRank", myRank);
        req.setAttribute("studentCount", board.size());
        req.setAttribute("reviews", reviews.findByReviewee(account.getId(), 5));
        render(req, resp, "student-profile");
    }
}
