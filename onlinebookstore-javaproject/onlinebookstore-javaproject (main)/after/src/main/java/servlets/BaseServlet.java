package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.model.UserRole;
import com.bittercode.service.BookService;
import com.bittercode.service.UserService;
import com.bittercode.service.impl.BookServiceImpl;
import com.bittercode.service.impl.UserServiceImpl;
import com.bittercode.util.StoreUtil;

/**
 * Base Servlet class implementing Template Method pattern
 * Provides common functionality for all servlets including:
 * - Authentication and authorization
 * - Standard request processing flow
 * - Error handling
 * - Service access
 */
public abstract class BaseServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(BaseServlet.class.getName());
    
    private BookService bookService;
    private UserService userService;
    
    public BaseServlet() {
        this.bookService = new BookServiceImpl();
        this.userService = new UserServiceImpl();
    }
    
    /**
     * Template method for processing requests
     * Defines the standard flow for all servlet requests
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
        
        try {
            UserRole requiredRole = getRequiredRole();
            if (requiredRole != null && !StoreUtil.isLoggedIn(requiredRole, req.getSession())) {
                handleNotLoggedIn(req, res, pw, requiredRole);
                return;
            }
            
            includeHeaderAndMenu(req, res, pw);
            doBusinessLogic(req, res, pw);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing request", e);
            handleError(req, res, pw, e);
        }
    }
    
    /**
     * Business logic to be implemented by child servlets
     * 
     * @param req HTTP request
     * @param res HTTP response
     * @param pw PrintWriter for output
     */
    protected abstract void doBusinessLogic(HttpServletRequest req, HttpServletResponse res, PrintWriter pw) 
            throws ServletException, IOException;
    
    /**
     * Returns the role required to access this servlet
     * 
     * @return UserRole required, or null if no authentication needed
     */
    protected abstract UserRole getRequiredRole();
    
    /**
     * Returns the name of the active tab for this servlet
     * 
     * @return String representing the active tab name
     */
    protected abstract String getActiveTabName();
    
    /**
     * Handles unauthenticated access attempts
     * Displays login page with appropriate message
     */
    protected void handleNotLoggedIn(HttpServletRequest req, HttpServletResponse res, PrintWriter pw, UserRole role) 
            throws ServletException, IOException {
        String loginPage = UserRole.SELLER.equals(role) ? "SellerLogin.html" : "CustomerLogin.html";
        RequestDispatcher rd = req.getRequestDispatcher(loginPage);
        rd.include(req, res);
        pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!</td></tr></table>");
    }
    
    /**
     * Includes header and menu in the response
     * Sets the active tab based on current servlet
     */
    protected void includeHeaderAndMenu(HttpServletRequest req, HttpServletResponse res, PrintWriter pw) 
            throws ServletException, IOException {
        UserRole role = getRequiredRole();
        String homePage = UserRole.SELLER.equals(role) ? "SellerHome.html" : "CustomerHome.html";
        RequestDispatcher rd = req.getRequestDispatcher(homePage);
        rd.include(req, res);
        
        String activeTab = getActiveTabName();
        if (activeTab != null) {
            StoreUtil.setActiveTab(pw, activeTab);
        }
    }
    
    /**
     * Handles exceptions by forwarding to error handler
     */
    protected void handleError(HttpServletRequest req, HttpServletResponse res, PrintWriter pw, Exception e) 
            throws ServletException, IOException {
        logger.log(Level.SEVERE, "Error in servlet", e);
        req.setAttribute("javax.servlet.error.exception", e);
        req.setAttribute("javax.servlet.error.status_code", 500);
        RequestDispatcher rd = req.getRequestDispatcher("/errorHandler");
        rd.forward(req, res);
    }
    
    /**
     * Returns the book service instance
     */
    protected BookService getBookService() {
        return this.bookService;
    }
    
    /**
     * Returns the user service instance
     */
    protected UserService getUserService() {
        return this.userService;
    }
    
    /**
     * Gets attribute from session
     * 
     * @param req HTTP request
     * @param name Attribute name
     * @return Attribute value or null if not found
     */
    protected Object getSessionAttribute(HttpServletRequest req, String name) {
        HttpSession session = req.getSession();
        return session.getAttribute(name);
    }
    
    /**
     * Sets attribute in session
     * 
     * @param req HTTP request
     * @param name Attribute name
     * @param value Attribute value
     */
    protected void setSessionAttribute(HttpServletRequest req, String name, Object value) {
        HttpSession session = req.getSession();
        session.setAttribute(name, value);
    }
} 