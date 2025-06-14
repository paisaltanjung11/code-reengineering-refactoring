package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bittercode.model.Book;
import com.bittercode.model.Cart;
import com.bittercode.model.UserRole;
import com.bittercode.util.StoreUtil;

/**
 * Servlet for managing shopping cart functionality
 * Handles displaying, adding, and removing items from cart
 */
public class CartServlet extends BaseServlet {
    private static final Logger logger = Logger.getLogger(CartServlet.class.getName());

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        processRequest(req, res);
    }

    @Override
    protected void doBusinessLogic(HttpServletRequest req, HttpServletResponse res, PrintWriter pw) 
            throws ServletException, IOException {
        try {
            StoreUtil.updateCartItems(req);
            
            HttpSession session = req.getSession();
            String bookIds = getBookIdsFromSession(session);
            
            List<Book> books = getBookService().getBooksByCommaSeperatedBookIds(bookIds);
            List<Cart> cartItems = new ArrayList<>();
            
            renderCartHeader(pw);
            double amountToPay = renderCartItems(books, cartItems, session, pw);
            renderCheckoutSection(amountToPay, pw);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing cart", e);
            pw.println("<div class='alert alert-danger'>Error processing cart. Please try again later.</div>");
        }
    }
    
    /**
     * Gets comma-separated book IDs from session
     */
    private String getBookIdsFromSession(HttpSession session) {
        return session.getAttribute("items") != null 
                ? (String) session.getAttribute("items") 
                : "";
    }
    
    /**
     * Renders the cart header
     */
    private void renderCartHeader(PrintWriter pw) {
        pw.println("<div id='topmid' style='background-color:grey'>Shopping Cart</div>");
        pw.println("<table class=\"table table-hover\" style='background-color:white'>\r\n"
                + "  <thead>\r\n"
                + "    <tr style='background-color:black; color:white;'>\r\n"
                + "      <th scope=\"col\">BookId</th>\r\n"
                + "      <th scope=\"col\">Name</th>\r\n"
                + "      <th scope=\"col\">Author</th>\r\n"
                + "      <th scope=\"col\">Price/Item</th>\r\n"
                + "      <th scope=\"col\">Quantity</th>\r\n"
                + "      <th scope=\"col\">Amount</th>\r\n"
                + "    </tr>\r\n"
                + "  </thead>\r\n"
                + "  <tbody>\r\n");
    }
    
    /**
     * Renders cart items and calculates total amount
     * 
     * @return Total amount to pay
     */
    private double renderCartItems(List<Book> books, List<Cart> cartItems, HttpSession session, PrintWriter pw) {
        double amountToPay = 0;
        
        if (books == null || books.isEmpty()) {
            pw.println("    <tr style='background-color:green'>\r\n"
                    + "      <th scope=\"row\" colspan='6' style='color:yellow; text-align:center;'> No Items In the Cart </th>\r\n"
                    + "    </tr>\r\n");
            return amountToPay;
        }
        
        for (Book book : books) {
            int qty = (int) session.getAttribute("qty_" + book.getBarcode());
            Cart cart = new Cart(book, qty);
            cartItems.add(cart);
            amountToPay += (qty * book.getPrice());
            pw.println(createCartItemRow(cart));
        }
        
        // Store cart data in session for checkout
        session.setAttribute("cartItems", cartItems);
        session.setAttribute("amountToPay", amountToPay);
        
        if (amountToPay > 0) {
            pw.println(createTotalAmountRow(amountToPay));
        }
        
        return amountToPay;
    }
    
    /**
     * Creates HTML for the total amount row
     */
    private String createTotalAmountRow(double amount) {
        return "    <tr style='background-color:green'>\r\n"
                + "      <th scope=\"row\" colspan='5' style='color:yellow; text-align:center;'> Total Amount To Pay </th>\r\n"
                + "      <td colspan='1' style='color:white; font-weight:bold'><span>&#8377;</span> "
                + amount
                + "</td>\r\n"
                + "    </tr>\r\n";
    }
    
    /**
     * Renders the checkout button section
     */
    private void renderCheckoutSection(double amountToPay, PrintWriter pw) {
        pw.println("  </tbody>\r\n" + "</table>");
        
        if (amountToPay > 0) {
            pw.println("<div style='text-align:right; margin-right:20px;'>\r\n"
                    + "<form action=\"checkout\" method=\"post\">"
                    + "<input type='submit' class=\"btn btn-primary\" name='pay' value='Proceed to Pay &#8377; "
                    + amountToPay + "'/></form>"
                    + "    </div>");
        }
    }

    @Override
    protected UserRole getRequiredRole() {
        return UserRole.CUSTOMER;
    }

    @Override
    protected String getActiveTabName() {
        return "cart";
    }

    /**
     * Creates HTML for a cart item row
     */
    private String createCartItemRow(Cart cart) {
        Book book = cart.getBook();
        return "    <tr>\r\n"
                + "      <th scope=\"row\">" + book.getBarcode() + "</th>\r\n"
                + "      <td>" + book.getName() + "</td>\r\n"
                + "      <td>" + book.getAuthor() + "</td>\r\n"
                + "      <td><span>&#8377;</span> " + book.getPrice() + "</td>\r\n"
                + "      <td><form method='post' action='cart'><button type='submit' name='removeFromCart' class=\"glyphicon glyphicon-minus btn btn-danger\"></button> "
                + "<input type='hidden' name='selectedBookId' value='" + book.getBarcode() + "'/>"
                + cart.getQuantity()
                + " <button type='submit' name='addToCart' class=\"glyphicon glyphicon-plus btn btn-success\"></button></form></td>\r\n"
                + "      <td><span>&#8377;</span> " + (book.getPrice() * cart.getQuantity()) + "</td>\r\n"
                + "    </tr>\r\n";
    }
}

