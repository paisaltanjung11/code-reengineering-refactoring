package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.constant.ResponseCode;
import com.bittercode.constant.db.UsersDBConstants;
import com.bittercode.model.User;
import com.bittercode.model.UserRole;
import com.bittercode.model.Email;
import com.bittercode.model.PhoneNumber;
import com.bittercode.service.UserService;
import com.bittercode.service.impl.UserServiceImpl;
import servlets.base.BaseServlet;

public class CustomerRegisterServlet extends BaseServlet {
    private final UserService userService;
    
    public CustomerRegisterServlet() {
        this.userService = new UserServiceImpl();
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);

        try {
            User user = createUserFromRequest(req);
            String respCode = userService.register(UserRole.CUSTOMER, user);
            handleRegistrationResponse(req, res, pw, respCode);
        } catch (IllegalArgumentException e) {
            handleInvalidInput(req, res, pw, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            handleInvalidInput(req, res, pw, "Registration failed. Please try again.");
        }
    }

    private User createUserFromRequest(HttpServletRequest req) {
        String pWord = req.getParameter(UsersDBConstants.COLUMN_PASSWORD);
        String fName = req.getParameter(UsersDBConstants.COLUMN_FIRSTNAME);
        String lName = req.getParameter(UsersDBConstants.COLUMN_LASTNAME);
        String addr = req.getParameter(UsersDBConstants.COLUMN_ADDRESS);
        PhoneNumber phNo = new PhoneNumber(req.getParameter(UsersDBConstants.COLUMN_PHONE));
        Email mailId = new Email(req.getParameter(UsersDBConstants.COLUMN_MAILID));

        User user = new User();
        user.setEmailId(mailId.toString());
        user.setFirstName(fName);
        user.setLastName(lName);
        user.setPassword(pWord);
        user.setPhone(phNo.toLong());
        user.setAddress(addr);
        
        return user;
    }

    private void handleRegistrationResponse(HttpServletRequest req, HttpServletResponse res, PrintWriter pw, String respCode) throws ServletException, IOException {
        if (ResponseCode.SUCCESS.name().equalsIgnoreCase(respCode)) {
            includePage(req, res, "CustomerLogin.html", pw, "register");
            showSuccessMessage(pw, "User Registered Successfully");
        } else {
            handleInvalidInput(req, res, pw, respCode);
        }
    }

    private void handleInvalidInput(HttpServletRequest req, HttpServletResponse res, PrintWriter pw, String message) throws ServletException, IOException {
        includePage(req, res, "CustomerRegister.html", pw, "register");
        showErrorMessage(pw, message);
    }
}