package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.bittercode.model.Book;
import com.bittercode.model.UserRole;
import com.bittercode.service.BookService;
import com.bittercode.service.TemplateService;
import com.bittercode.service.impl.BookServiceImpl;
import com.bittercode.util.StoreUtil;

public class ViewBookServlet extends AbstractBaseServlet {
    
    private final BookService bookService;
    private HttpServletRequest request;
    
    public ViewBookServlet() {
        this.bookService = new BookServiceImpl();
    }
    
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType("text/html");
        this.request = req;
        
        validateUserAccess(req, res, UserRole.CUSTOMER);
        
        try {
            List<Book> books = bookService.getAllBooks();
            renderPage(req, res, "CustomerHome.html", "books");
            renderBookList(pw, books, req.getSession());
        } catch (Exception e) {
            throw new ServletException("Error processing book view", e);
        }
    }
    
    private void renderBookList(PrintWriter pw, List<Book> books, HttpSession session) {
        pw.println("<div id='topmid' style='background-color:grey'>Available Books"
                + createCartButton()
                + "</div>");
        pw.println("<div class=\"container\">\r\n"
                + "        <div class=\"card-columns\">");
                
        StoreUtil.updateCartItems(request);
        
        for (Book book : books) {
            int cartQuantity = getCartItemQuantity(session, book.getBarcode());
            pw.println(TemplateService.createBookCard(book, cartQuantity));
        }
        
        pw.println("</div>" + createCheckoutButton());
    }
    
    private String createCartButton() {
        return "<form action=\"cart\" method=\"post\" style='float:right; margin-right:20px'>"
                + "<input type='submit' class=\"btn btn-primary\" name='cart' value='Proceed'/></form>";
    }
    
    private String createCheckoutButton() {
        return "<div style='float:auto'><form action=\"cart\" method=\"post\">"
                + "<input type='submit' class=\"btn btn-success\" name='cart' value='Proceed to Checkout'/></form>"
                + "    </div>";
    }
    
    private int getCartItemQuantity(HttpSession session, String bookCode) {
        return session.getAttribute("qty_" + bookCode) != null 
                ? (int) session.getAttribute("qty_" + bookCode) 
                : 0;
    }
}
