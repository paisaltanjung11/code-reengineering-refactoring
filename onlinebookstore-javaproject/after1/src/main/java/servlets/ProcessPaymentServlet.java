package servlets;

import java.io.IOException;
import java.io.PrintWriter;
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

public class ProcessPaymentServlet extends AbstractBaseServlet {

    private final BookService bookService;

    public ProcessPaymentServlet() {
        this.bookService = new BookServiceImpl();
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);

        validateUserAccess(req, res, UserRole.CUSTOMER);
        
        try {
            renderPage(req, res, "CustomerHome.html", "cart");
            processPayment(pw, req.getSession());
        } catch (Exception e) {
            throw new ServletException("Error processing payment", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void processPayment(PrintWriter pw, HttpSession session) throws Exception {
        List<Cart> cartItems = (List<Cart>) session.getAttribute("cartItems");
        if (cartItems == null || cartItems.isEmpty()) {
            return;
        }

        pw.println("<div id='topmid' style='background-color:grey'>Your Orders</div>");
        pw.println("<div class=\"container\">\r\n"
                + "        <div class=\"card-columns\">");

        for (Cart cart : cartItems) {
            updateBookInventory(cart);
            pw.println(TemplateService.createOrderCard(cart));
        }

        clearCartSession(session);
        pw.println("</div>\r\n" + "    </div>");
    }

    private void updateBookInventory(Cart cart) throws Exception {
        Book book = cart.getBook();
        int newQuantity = book.getQuantity() - cart.getQuantity();
        bookService.updateBookQtyById(book.getBarcode(), newQuantity);
    }

    @SuppressWarnings("unchecked")
    private void clearCartSession(HttpSession session) {
        List<Cart> cartItems = (List<Cart>) session.getAttribute("cartItems");
        for (Cart cart : cartItems) {
            session.removeAttribute("qty_" + cart.getBook().getBarcode());
        }
        session.removeAttribute("amountToPay");
        session.removeAttribute("cartItems");
        session.removeAttribute("items");
        session.removeAttribute("selectedBookId");
    }
}
