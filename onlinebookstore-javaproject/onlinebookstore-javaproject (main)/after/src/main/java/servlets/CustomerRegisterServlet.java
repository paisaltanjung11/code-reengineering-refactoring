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

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.constant.ResponseCode;
import com.bittercode.constant.db.UsersDBConstants;
import com.bittercode.model.User;
import com.bittercode.model.UserRole;
import com.bittercode.service.UserService;
import com.bittercode.service.impl.UserServiceImpl;

/**
 * Servlet for customer registration
 * Refactored to fix syntax errors and improve structure
 */
public class CustomerRegisterServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CustomerRegisterServlet.class.getName());
    private UserService userService = new UserServiceImpl();

    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);

        try {
            User user = collectUserDetails(req);
            
            String respCode = userService.register(UserRole.CUSTOMER, user);
            logger.info("Registration response: " + respCode);
            
            handleRegistrationResponse(req, res, pw, respCode);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid phone number format", e);
            showErrorPage(req, res, pw, "Invalid phone number format. Please enter a valid number.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during customer registration", e);
            showErrorPage(req, res, pw, "An error occurred during registration. Please try again.");
        }
    }
    

    private User collectUserDetails(HttpServletRequest req) {
        String pWord = req.getParameter(UsersDBConstants.COLUMN_PASSWORD);
        String fName = req.getParameter(UsersDBConstants.COLUMN_FIRSTNAME);
        String lName = req.getParameter(UsersDBConstants.COLUMN_LASTNAME);
        String addr = req.getParameter(UsersDBConstants.COLUMN_ADDRESS);
        String phNo = req.getParameter(UsersDBConstants.COLUMN_PHONE);
        String mailId = req.getParameter(UsersDBConstants.COLUMN_MAILID);
        
        User user = new User();
        user.setEmailId(mailId);
        user.setFirstName(fName);
        user.setLastName(lName);
        user.setPassword(pWord);
        user.setPhone(Long.parseLong(phNo));
        user.setAddress(addr);
        
        return user;
    }
    

    private void handleRegistrationResponse(HttpServletRequest req, HttpServletResponse res, PrintWriter pw, String respCode) 
            throws ServletException, IOException {
        if (ResponseCode.SUCCESS.name().equalsIgnoreCase(respCode)) {
            RequestDispatcher rd = req.getRequestDispatcher("CustomerLogin.html");
            rd.include(req, res);
            pw.println("<table class=\"tab\"><tr><td>User Registered Successfully</td></tr></table>");
        } else {
            showErrorPage(req, res, pw, respCode);
        }
    }

    private void showErrorPage(HttpServletRequest req, HttpServletResponse res, PrintWriter pw, String errorMessage) 
            throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("CustomerRegister.html");
        rd.include(req, res);
        pw.println("<table class=\"tab\"><tr><td>" + errorMessage + "</td></tr></table>");
        pw.println("Sorry for interruption! Try again");
    }
}