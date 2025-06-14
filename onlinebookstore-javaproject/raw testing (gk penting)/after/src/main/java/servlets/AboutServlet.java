package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bittercode.model.UserRole;
import com.bittercode.util.StoreUtil;

public class AboutServlet extends HttpServlet {

    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType("text/html");
        if (StoreUtil.isLoggedIn(UserRole.CUSTOMER, req.getSession())) {
            includePage(req, res, "CustomerHome.html", pw, "about");
        } else if (StoreUtil.isLoggedIn(UserRole.SELLER, req.getSession())) {
            includePage(req, res, "SellerHome.html", pw, "about");
        } else {
            redirectToLogin(req, res, pw);
        }
    }

    private void includePage(HttpServletRequest req, HttpServletResponse res, String page, PrintWriter pw, String tab) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher(page);
        rd.include(req, res);
        StoreUtil.setActiveTab(pw, tab);
        pw.println("<iframe src=\"https://flowcv.me/shashirajraja\" class=\"holds-the-iframe\"\r\n"
                + "        title=\"My Personal Website\" width=\"100%\" height=\"100%\"></iframe>");
    }

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res, PrintWriter pw) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("login.html");
        rd.include(req, res);
        pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!</td></tr></table>");
    }

}
