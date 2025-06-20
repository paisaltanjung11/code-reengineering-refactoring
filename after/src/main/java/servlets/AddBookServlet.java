package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.constant.db.BooksDBConstants;
import com.bittercode.model.Book;
import com.bittercode.model.UserRole;
import com.bittercode.service.BookService;
import com.bittercode.service.impl.BookServiceImpl;
import com.bittercode.util.StoreUtil;


public class AddBookServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(AddBookServlet.class.getName());
    private BookService bookService = new BookServiceImpl();

    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);

        if (!StoreUtil.isLoggedIn(UserRole.SELLER, req.getSession())) {
            handleNotLoggedIn(req, res, pw);
            return;
        }

        String bName = req.getParameter(BooksDBConstants.COLUMN_NAME);
        RequestDispatcher rd = req.getRequestDispatcher("SellerHome.html");
        rd.include(req, res);
        StoreUtil.setActiveTab(pw, "addbook");
        pw.println("<div class='container my-2'>");
        
        if(bName == null || bName.trim().isEmpty()) {
            showAddBookForm(pw);
            return;
        }
        
        try {
            processAddBook(req, pw);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing add book request", e);
            pw.println("<table class=\"tab\"><tr><td>Failed to Add Books! Fill up CareFully</td></tr></table>");
        }
        
        pw.println("</div>");
    }
    

    private void processAddBook(HttpServletRequest req, PrintWriter pw) {
        try {
            Book book = createBookFromRequest(req);
            String message = bookService.addBook(book);
            
            if ("SUCCESS".equalsIgnoreCase(message)) {
                pw.println(
                        "<table class=\"tab\"><tr><td>Book Detail Updated Successfully!<br/>Add More Books</td></tr></table>");
            } else {
                pw.println("<table class=\"tab\"><tr><td>Failed to Add Books! Fill up CareFully</td></tr></table>");
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid number format in book data", e);
            pw.println("<table class=\"tab\"><tr><td>Invalid price or quantity format. Please enter valid numbers.</td></tr></table>");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding book", e);
            pw.println("<table class=\"tab\"><tr><td>Failed to Add Books! Fill up CareFully</td></tr></table>");
        }
    }
    
  
    private Book createBookFromRequest(HttpServletRequest req) {
        String uniqueID = UUID.randomUUID().toString();
        String bName = req.getParameter(BooksDBConstants.COLUMN_NAME);
        String bAuthor = req.getParameter(BooksDBConstants.COLUMN_AUTHOR);
        double bPrice = Double.parseDouble(req.getParameter(BooksDBConstants.COLUMN_PRICE));
        int bQty = Integer.parseInt(req.getParameter(BooksDBConstants.COLUMN_QUANTITY));

        return new Book(uniqueID, bName, bAuthor, bPrice, bQty);
    }
    

    private void handleNotLoggedIn(HttpServletRequest req, HttpServletResponse res, PrintWriter pw) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("SellerLogin.html");
        rd.include(req, res);
        pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!</td></tr></table>");
    }
    

    private static void showAddBookForm(PrintWriter pw) {
        String form = "<table class=\"tab my-5\" style=\"width:40%;\">\r\n"
                + "        <tr>\r\n"
                + "            <td>\r\n"
                + "                <form action=\"addbook\" method=\"post\">\r\n"
                + "                    <label for=\"bookName\">Book Name : </label> <input type=\"text\" name=\"name\" id=\"bookName\" placeholder=\"Enter Book's name\" required><br/>\r\n"
                + "                    <label for=\"bookAuthor\">Book Author : </label><input type=\"text\" name=\"author\" id=\"bookAuthor\" placeholder=\"Enter Author's Name\" required><br/>\r\n"
                + "                    <label for=\"bookPrice\">Book Price : </label><input type=\"number\" name=\"price\" placeholder=\"Enter the Price\" required><br/>\r\n"
                + "                    <label for=\"bookQuantity\">Book Qnty : </label><input type=\"number\" name=\"quantity\" id=\"bookQuantity\" placeholder=\"Enter the quantity\" required><br/>\r\n"
                + "                    <input class=\"btn btn-success my-2\" type=\"submit\" value=\" Add Book \">\r\n"
                + "                </form>\r\n"
                + "            </td>\r\n"
                + "        </tr>  \r\n"
                + "    </table>";
        pw.println(form);
    }
}
