package servlets.base;

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

public abstract class BaseServlet extends HttpServlet {
    
    protected void redirectToLogin(HttpServletRequest req, HttpServletResponse res, PrintWriter pw, String loginPage) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher(loginPage);
        rd.include(req, res);
        pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!</td></tr></table>");
    }
    
    protected boolean isUserLoggedIn(UserRole role, HttpServletRequest req) {
        return StoreUtil.isLoggedIn(role, req.getSession());
    }
    
    protected void includePage(HttpServletRequest req, HttpServletResponse res, String page, PrintWriter pw, String activeTab) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher(page);
        rd.include(req, res);
        StoreUtil.setActiveTab(pw, activeTab);
    }
    
    protected void showMessage(PrintWriter pw, String message, String cssClass) {
        pw.println("<table class=\"" + cssClass + "\"><tr><td>" + message + "</td></tr></table>");
    }
    
    protected void showSuccessMessage(PrintWriter pw, String message) {
        showMessage(pw, message, "tab success");
    }
    
    protected void showErrorMessage(PrintWriter pw, String message) {
        showMessage(pw, message, "tab error");
    }
    
    protected HttpSession getOrCreateSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            session = req.getSession(true);
        }
        return session;
    }
} 