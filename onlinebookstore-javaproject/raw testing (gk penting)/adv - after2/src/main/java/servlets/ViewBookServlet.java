package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bittercode.model.Book;
import com.bittercode.model.UserRole;
import com.bittercode.service.BookService;
import com.bittercode.service.impl.BookServiceImpl;
import com.bittercode.util.StoreUtil;

/**
 * Servlet for displaying available books to customers
 * Extends BaseServlet to leverage common functionality
 */
public class ViewBookServlet extends BaseServlet {
    private static final Logger logger = Logger.getLogger(ViewBookServlet.class.getName());

    // book service for database operations and logics
    BookService bookService = new BookServiceImpl();

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        processRequest(req, res);
    }

    @Override
    protected void doBusinessLogic(HttpServletRequest req, HttpServletResponse res, PrintWriter pw) 
            throws ServletException, IOException {
        try {
            StoreUtil.updateCartItems(req);
            List<Book> books = getBookService().getAllBooks();

            renderBookListHeader(pw);
            renderBooks(req.getSession(), books, pw);
            renderCheckoutButton(pw);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error displaying books", e);
            pw.println("<div class='alert alert-danger'>Error loading books. Please try again later.</div>");
        }
    }

    @Override
    protected UserRole getRequiredRole() {
        return UserRole.CUSTOMER;
    }

    @Override
    protected String getActiveTabName() {
        return "books";
    }
    
    /**
     * Renders the header section of the book list page
     */
    private void renderBookListHeader(PrintWriter pw) {
        pw.println("<div id='topmid' style='background-color:grey'>Available Books"
                + "<form action=\"cart\" method=\"post\" style='float:right; margin-right:20px'>"
                + "<input type='submit' class=\"btn btn-primary\" name='cart' value='Proceed'/></form>"
                + "</div>");
        pw.println("<div class=\"container\">\r\n"
                + "        <div class=\"card-columns\">");
    }
    
    /**
     * Renders all books in the catalog
     */
    private void renderBooks(HttpSession session, List<Book> books, PrintWriter pw) {
        for (Book book : books) {
            pw.println(createBookCard(session, book));
        }
    }
    
    /**
     * Renders the checkout button at the bottom of the page
     */
    private void renderCheckoutButton(PrintWriter pw) {
        pw.println("</div>"
                + "<div style='float:auto'><form action=\"cart\" method=\"post\">"
                + "<input type='submit' class=\"btn btn-success\" name='cart' value='Proceed to Checkout'/></form>"
                + "    </div>");
    }

    /**
     * Creates HTML for a book card with add-to-cart functionality
     * 
     * @param session The current HTTP session
     * @param book The book to display
     * @return HTML string representing the book card
     */
    private String createBookCard(HttpSession session, Book book) {
        String bCode = book.getBarcode();
        int bQty = book.getQuantity();
        int cartItemQty = getCartItemQuantity(session, bCode);
        String actionButton = createActionButton(bCode, bQty, cartItemQty);

        return "<div class=\"card\">\r\n"
                + "                <div class=\"row card-body\">\r\n"
                + "                    <img class=\"col-sm-6\" src=\"logo.png\" alt=\"Card image cap\">\r\n"
                + "                    <div class=\"col-sm-6\">\r\n"
                + "                        <h5 class=\"card-title text-success\">" + book.getName() + "</h5>\r\n"
                + "                        <p class=\"card-text\">\r\n"
                + "                        Author: <span class=\"text-primary\" style=\"font-weight:bold;\"> "
                + book.getAuthor()
                + "</span><br>\r\n"
                + "                        </p>\r\n"
                + "                    </div>\r\n"
                + "                </div>\r\n"
                + "                <div class=\"row card-body\">\r\n"
                + "                    <div class=\"col-sm-6\">\r\n"
                + "                        <p class=\"card-text\">\r\n"
                + "                        <span>Id: " + bCode + "</span>\r\n"
                + createInventoryStatusHtml(bQty)
                + "                        </p>\r\n"
                + "                    </div>\r\n"
                + "                    <div class=\"col-sm-6\">\r\n"
                + "                        <p class=\"card-text\">\r\n"
                + "                        Price: <span style=\"font-weight:bold; color:green\"> &#8377; "
                + book.getPrice()
                + " </span>\r\n"
                + "                        </p>\r\n"
                + actionButton
                + "                    </div>\r\n"
                + "                </div>\r\n"
                + "            </div>";
    }
    
    /**
     * Gets the quantity of a book in the cart
     */
    private int getCartItemQuantity(HttpSession session, String bookId) {
        if (session.getAttribute("qty_" + bookId) != null) {
            return (int) session.getAttribute("qty_" + bookId);
        }
        return 0;
    }
    
    /**
     * Creates the HTML for the inventory status message
     */
    private String createInventoryStatusHtml(int quantity) {
        if (quantity < 20) {
            return "<br><span class=\"text-danger\">Only " + quantity + " items left</span>\r\n";
        } else {
            return "<br><span class=\"text-success\">Trending</span>\r\n";
        }
    }
    
    /**
     * Creates the appropriate action button based on inventory and cart status
     */
    private String createActionButton(String bookId, int inventoryQty, int cartQty) {
        if (inventoryQty <= 0) {
            return "<p class=\"btn btn-danger\">Out Of Stock</p>\r\n";
        }
        
        if (cartQty == 0) {
            return "<form action=\"viewbook\" method=\"post\">"
                    + "<input type='hidden' name='selectedBookId' value='" + bookId + "'>"
                    + "<input type='hidden' name='qty_" + bookId + "' value='1'/>"
                    + "<input type='submit' class=\"btn btn-primary\" name='addToCart' value='Add To Cart'/></form>";
        } else {
            return "<form method='post' action='cart'>"
                    + "<button type='submit' name='removeFromCart' class=\"glyphicon glyphicon-minus btn btn-danger\"></button> "
                    + "<input type='hidden' name='selectedBookId' value='" + bookId + "'/>"
                    + cartQty
                    + " <button type='submit' name='addToCart' class=\"glyphicon glyphicon-plus btn btn-success\"></button></form>";
        }
    }
}

