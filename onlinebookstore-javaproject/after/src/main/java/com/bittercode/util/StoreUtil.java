package com.bittercode.util;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bittercode.model.UserRole;

/**
 * Store Utility Class For Common Operations
 * Refactored to follow better OO principles
 */
public class StoreUtil {

    /**
     * Check if the User is logged in with the requested role
     */
    public static boolean isLoggedIn(UserRole role, HttpSession session) {
        if (role == null || session == null) {
            return false;
        }
        return session.getAttribute(role.toString()) != null;
    }

    /**
     * Modify the active tab in the page menu bar
     */
    public static void setActiveTab(PrintWriter pw, String activeTab) {
        if (pw == null || activeTab == null) {
            return;
        }
        
        pw.println("<script>document.getElementById(activeTab).classList.remove(\"active\");activeTab=" + activeTab
                + "</script>");
        pw.println("<script>document.getElementById('" + activeTab + "').classList.add(\"active\");</script>");
    }

    /**
     * Add/Remove/Update Item in the cart using the session
     */
    public static void updateCartItems(HttpServletRequest req) {
        if (req == null) {
            return;
        }
        
        String selectedBookId = req.getParameter("selectedBookId");
        HttpSession session = req.getSession();
        
        if (selectedBookId == null) {
            return;
        }
        
        if (req.getParameter("addToCart") != null) {
            addToCart(session, selectedBookId);
        } else if (req.getParameter("removeFromCart") != null) {
            removeFromCart(session, selectedBookId);
        }
    }
    
    /**
     * Add an item to the cart
     */
    private static void addToCart(HttpSession session, String selectedBookId) {
        // Items will contain comma separated bookIds that needs to be added in the cart
        String items = (String) session.getAttribute("items");
        if (items == null || items.isEmpty()) {
            items = selectedBookId;
        } else if (!items.contains(selectedBookId)) {
            items = items + "," + selectedBookId; // if items already contains bookId, don't add it
        }
        
        // set the items in the session to be used later
        session.setAttribute("items", items);

        /*
         * Quantity of each item in the cart will be stored in the session as:
         * Prefixed with qty_ following its bookId
         * For example 2 no. of book with id 'myBook' in the cart will be
         * added to the session as qty_myBook=2
         */
        int itemQty = 0;
        if (session.getAttribute("qty_" + selectedBookId) != null) {
            itemQty = (int) session.getAttribute("qty_" + selectedBookId);
        }
        itemQty += 1;
        session.setAttribute("qty_" + selectedBookId, itemQty);
    }
    
    /**
     * Remove an item from the cart
     */
    private static void removeFromCart(HttpSession session, String selectedBookId) {
        // Get the current quantity of the selected book
        int itemQty = 0;
        if (session.getAttribute("qty_" + selectedBookId) != null) {
            itemQty = (int) session.getAttribute("qty_" + selectedBookId);
        }
        
        // Reduce quantity by 1
        itemQty = itemQty - 1;
        
        // If quantity becomes zero, remove the item completely
        if (itemQty <= 0) {
            session.removeAttribute("qty_" + selectedBookId);
            
            // Remove from comma-separated items list
            String items = (String) session.getAttribute("items");
            if (items != null && items.contains(selectedBookId)) {
                items = items.replace(selectedBookId + ",", "");
                items = items.replace("," + selectedBookId, "");
                items = items.replace(selectedBookId, "");
                session.setAttribute("items", items);
            }
        } else {
            // Update the reduced quantity
            session.setAttribute("qty_" + selectedBookId, itemQty);
        }
    }
}
