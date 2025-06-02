package com.bittercode.service;

import com.bittercode.model.Book;
import com.bittercode.model.Cart;

public class TemplateService {
    
    public static String createBookCard(Book book, int cartQuantity) {
        return "<div class=\"card\">\r\n"
                + createBookHeader(book)
                + createBookDetails(book, cartQuantity)
                + "</div>";
    }
    
    public static String createBookHeader(Book book) {
        return "<div class=\"row card-body\">\r\n"
                + "    <img class=\"col-sm-6\" src=\"logo.png\" alt=\"Book cover\">\r\n"
                + "    <div class=\"col-sm-6\">\r\n"
                + "        <h5 class=\"card-title text-success\">" + book.getName() + "</h5>\r\n"
                + "        <p class=\"card-text\">\r\n"
                + "        Author: <span class=\"text-primary\" style=\"font-weight:bold;\">"
                + book.getAuthor()
                + "</span><br>\r\n"
                + "        </p>\r\n"
                + "    </div>\r\n"
                + "</div>\r\n";
    }
    
    public static String createBookDetails(Book book, int cartQuantity) {
        return "<div class=\"row card-body\">\r\n"
                + "    <div class=\"col-sm-6\">\r\n"
                + "        <p class=\"card-text\">\r\n"
                + "        <span>Id: " + book.getBarcode() + "</span>\r\n"
                + createStockStatus(book.getQuantity())
                + "        </p>\r\n"
                + "    </div>\r\n"
                + "    <div class=\"col-sm-6\">\r\n"
                + "        <p class=\"card-text\">\r\n"
                + "        Price: <span style=\"font-weight:bold; color:green\"> &#8377; "
                + book.getPrice()
                + " </span>\r\n"
                + "        </p>\r\n"
                + createActionButton(book, cartQuantity)
                + "    </div>\r\n"
                + "</div>\r\n";
    }
    
    public static String createStockStatus(int quantity) {
        return quantity < 20 
                ? "<br><span class=\"text-danger\">Only " + quantity + " items left</span>\r\n"
                : "<br><span class=\"text-success\">Trending</span>\r\n";
    }
    
    public static String createActionButton(Book book, int cartQuantity) {
        if (book.getQuantity() <= 0) {
            return "<p class=\"btn btn-danger\">Out Of Stock</p>\r\n";
        }
        
        if (cartQuantity == 0) {
            return "<form action=\"viewbook\" method=\"post\">"
                    + "<input type='hidden' name='selectedBookId' value='" + book.getBarcode() + "'>"
                    + "<input type='hidden' name='qty_" + book.getBarcode() + "' value='1'/>"
                    + "<input type='submit' class=\"btn btn-primary\" name='addToCart' value='Add To Cart'/></form>";
        }
        
        return "<form method='post' action='cart'>"
                + "<button type='submit' name='removeFromCart' class=\"glyphicon glyphicon-minus btn btn-danger\"></button> "
                + "<input type='hidden' name='selectedBookId' value='" + book.getBarcode() + "'/>"
                + cartQuantity
                + " <button type='submit' name='addToCart' class=\"glyphicon glyphicon-plus btn btn-success\"></button></form>";
    }
    
    public static String createCartItemRow(Cart cart) {
        Book book = cart.getBook();
        return "<tr>\r\n"
                + "    <th scope=\"row\">" + book.getBarcode() + "</th>\r\n"
                + "    <td>" + book.getName() + "</td>\r\n"
                + "    <td>" + book.getAuthor() + "</td>\r\n"
                + "    <td><span>&#8377;</span> " + book.getPrice() + "</td>\r\n"
                + "    <td>" + createQuantityControls(book.getBarcode(), cart.getQuantity()) + "</td>\r\n"
                + "    <td><span>&#8377;</span> " + (book.getPrice() * cart.getQuantity()) + "</td>\r\n"
                + "</tr>\r\n";
    }
    
    public static String createQuantityControls(String bookCode, int quantity) {
        return "<form method='post' action='cart'>"
                + "<button type='submit' name='removeFromCart' class=\"glyphicon glyphicon-minus btn btn-danger\"></button> "
                + "<input type='hidden' name='selectedBookId' value='" + bookCode + "'/>"
                + quantity
                + " <button type='submit' name='addToCart' class=\"glyphicon glyphicon-plus btn btn-success\"></button></form>";
    }
    
    public static String createOrderCard(Cart cart) {
        Book book = cart.getBook();
        return "<div class=\"card\">\r\n"
                + createBookHeader(book)
                + createOrderDetails(book)
                + "</div>";
    }
    
    public static String createOrderDetails(Book book) {
        return "<div class=\"row card-body\">\r\n"
                + "    <div class=\"col-sm-6\">\r\n"
                + "        <p class=\"card-text\">\r\n"
                + "        <span style='color:blue;'>Order Id: ORD" + book.getBarcode() + "TM </span>\r\n"
                + "        <br><span class=\"text-danger\">Item Yet to be Delivered</span>\r\n"
                + "        </p>\r\n"
                + "    </div>\r\n"
                + "    <div class=\"col-sm-6\">\r\n"
                + "        <p class=\"card-text\">\r\n"
                + "        Amount Paid: <span style=\"font-weight:bold; color:green\"> &#8377; "
                + book.getPrice()
                + " </span>\r\n"
                + "        </p>\r\n"
                + "        <a href=\"#\" class=\"btn btn-info\">Order Placed</a>\r\n"
                + "    </div>\r\n"
                + "</div>\r\n";
    }
} 