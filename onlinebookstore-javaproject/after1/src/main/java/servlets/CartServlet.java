package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.bittercode.constant.BookStoreConstants;
import com.bittercode.model.Book;
import com.bittercode.model.Cart;
import com.bittercode.model.UserRole;
import com.bittercode.service.BookService;
import com.bittercode.service.TemplateService;
import com.bittercode.service.impl.BookServiceImpl;
import com.bittercode.util.StoreUtil;

public class CartServlet extends AbstractBaseServlet {

    private final BookService bookService;
    private HttpServletRequest request;

    public CartServlet() {
        this.bookService = new BookServiceImpl();
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
        this.request = req;

        validateUserAccess(req, res, UserRole.CUSTOMER);
        
        try {
            renderPage(req, res, "CustomerHome.html", "cart");
            processCart(pw, req.getSession());
        } catch (Exception e) {
            throw new ServletException("Error processing cart", e);
        }
    }

    private void processCart(PrintWriter pw, HttpSession session) throws Exception {
        StoreUtil.updateCartItems(request);
        
        String bookIds = (String) session.getAttribute("items");
        List<Book> books = bookService.getBooksByCommaSeperatedBookIds(bookIds != null ? bookIds : "");
        List<Cart> cartItems = new ArrayList<>();
        
        renderCartHeader(pw);
        double totalAmount = renderCartItems(pw, session, books, cartItems);
        
        session.setAttribute("cartItems", cartItems);
        session.setAttribute("amountToPay", totalAmount);
        
        if (totalAmount > 0) {
            renderPaymentButton(pw, totalAmount);
        }
    }

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

    private double renderCartItems(PrintWriter pw, HttpSession session, List<Book> books, List<Cart> cartItems) {
        double totalAmount = 0;
        
        if (books == null || books.isEmpty()) {
            renderEmptyCart(pw);
            return totalAmount;
        }

        for (Book book : books) {
            int quantity = (int) session.getAttribute("qty_" + book.getBarcode());
            Cart cart = new Cart(book, quantity);
            cartItems.add(cart);
            totalAmount += (quantity * book.getPrice());
            pw.println(TemplateService.createCartItemRow(cart));
        }

        if (totalAmount > 0) {
            renderTotalAmount(pw, totalAmount);
        }

        return totalAmount;
    }

    private void renderEmptyCart(PrintWriter pw) {
        pw.println("    <tr style='background-color:green'>\r\n"
                + "      <th scope=\"row\" colspan='6' style='color:yellow; text-align:center;'> No Items In the Cart </th>\r\n"
                + "    </tr>\r\n");
    }

    private void renderTotalAmount(PrintWriter pw, double amount) {
        pw.println("    <tr style='background-color:green'>\r\n"
                + "      <th scope=\"row\" colspan='5' style='color:yellow; text-align:center;'> Total Amount To Pay </th>\r\n"
                + "      <td colspan='1' style='color:white; font-weight:bold'><span>&#8377;</span> "
                + amount
                + "</td>\r\n"
                + "    </tr>\r\n");
    }

    private void renderPaymentButton(PrintWriter pw, double amount) {
        pw.println("</tbody>\r\n"
                + "</table>");
        pw.println("<div style='text-align:right; margin-right:20px;'>\r\n"
                + "<form action=\"checkout\" method=\"post\">"
                + "<input type='submit' class=\"btn btn-primary\" name='pay' value='Proceed to Pay &#8377; "
                + amount + "'/></form>"
                + "    </div>");
    }
}
