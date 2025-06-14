package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.bittercode.model.UserRole;
import com.bittercode.util.StoreUtil;

public abstract class AbstractBaseServlet extends HttpServlet {
    
    protected void validateUserAccess(HttpServletRequest req, HttpServletResponse res, UserRole requiredRole) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        if (!StoreUtil.isLoggedIn(requiredRole, req.getSession())) {
            String loginPage = requiredRole == UserRole.CUSTOMER ? "CustomerLogin.html" : "SellerLogin.html";
            RequestDispatcher rd = req.getRequestDispatcher(loginPage);
            rd.include(req, res);
            pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!</td></tr></table>");
            return;
        }
    }

    protected void renderPage(HttpServletRequest req, HttpServletResponse res, String template, String activeTab) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher(template);
        rd.include(req, res);
        if (activeTab != null) {
            StoreUtil.setActiveTab(res.getWriter(), activeTab);
        }
    }

    protected HttpSession getOrCreateSession(HttpServletRequest req) {
        return req.getSession(true);
    }
} 