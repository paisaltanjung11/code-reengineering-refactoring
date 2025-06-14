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

import com.bittercode.constant.ResponseCode;
import com.bittercode.model.UserRole;
import com.bittercode.service.BookService;
import com.bittercode.service.impl.BookServiceImpl;
import com.bittercode.util.StoreUtil;

/**
 * Servlet for removing books from the inventory
 * Only accessible by sellers
 */
public class RemoveBookServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RemoveBookServlet.class.getName());
    private BookService bookService = new BookServiceImpl();

    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType("text/html");
        
        // Validate seller login
        if (!StoreUtil.isLoggedIn(UserRole.SELLER, req.getSession())) {
            handleNotLoggedIn(req, res, pw);
            return;
        }

        try {
            String bookId = req.getParameter("bookId");
            RequestDispatcher rd = req.getRequestDispatcher("SellerHome.html");
            rd.include(req, res);
            StoreUtil.setActiveTab(pw, "removebook");
            pw.println("<div class='container'>");
            
            // If no book ID provided, show the form
            if (bookId == null || bookId.trim().isEmpty()) {
                showRemoveBookForm(pw);
                return;
            } 
            
            // Process book removal
            processRemoveBook(bookId.trim(), pw);
            
            pw.println("</div>");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in RemoveBookServlet", e);
            pw.println("<table class=\"tab\"><tr><td>Failed to Remove Books! Try Again</td></tr></table>");
        }
    }
    
    /**
     * Process book removal request
     * 
     * @param bookId ID of the book to remove
     * @param pw PrintWriter for output
     */
    private void processRemoveBook(String bookId, PrintWriter pw) {
        try {
            String responseCode = bookService.deleteBookById(bookId);
            if (ResponseCode.SUCCESS.name().equalsIgnoreCase(responseCode)) {
                showSuccessMessage(pw);
            } else {
                showNotFoundMessage(pw);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error removing book with ID: " + bookId, e);
            pw.println("<table class=\"tab\"><tr><td>Failed to Remove Book! Try Again</td></tr></table>");
        }
    }
    
    /**
     * Show success message after book removal
     */
    private void showSuccessMessage(PrintWriter pw) {
        pw.println("<table class=\"tab my-5\"><tr><td>Book Removed Successfully</td></tr></table>");
        pw.println("<table class=\"tab\"><tr><td><a href=\"removebook\">Remove more Books</a></td></tr></table>");
    }
    
    /**
     * Show not found message if book doesn't exist
     */
    private void showNotFoundMessage(PrintWriter pw) {
        pw.println("<table class=\"tab my-5\"><tr><td>Book Not Available In The Store</td></tr></table>");
        pw.println("<table class=\"tab\"><tr><td><a href=\"removebook\">Remove more Books</a></td></tr></table>");
    }
    
    /**
     * Handle case when user is not logged in
     */
    private void handleNotLoggedIn(HttpServletRequest req, HttpServletResponse res, PrintWriter pw) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("SellerLogin.html");
        rd.include(req, res);
        pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!</td></tr></table>");
    }

    /**
     * Display the remove book form
     */
    private static void showRemoveBookForm(PrintWriter pw) {
        String form = "<form action=\"removebook\" method=\"post\" class='my-5'>\r\n"
                + "        <table class=\"tab\">\r\n"
                + "        <tr>\r\n"
                + "            <td>\r\n"
                + "                <label for=\"bookCode\">Enter BookId to Remove </label>\r\n"
                + "                <input type=\"text\" name=\"bookId\" placeholder=\"Enter Book Id\" id=\"bookCode\" required>\r\n"
                + "                <input class=\"btn btn-danger my-2\" type=\"submit\" value=\"Remove Book\">\r\n"
                + "            </td>\r\n"
                + "        </tr>\r\n"
                + "        </table>\r\n"
                + "    </form>";
        pw.println(form);
    }
}

