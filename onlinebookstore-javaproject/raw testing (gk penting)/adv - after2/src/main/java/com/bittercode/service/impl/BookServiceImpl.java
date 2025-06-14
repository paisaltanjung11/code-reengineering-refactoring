package com.bittercode.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bittercode.constant.ResponseCode;
import com.bittercode.constant.db.BooksDBConstants;
import com.bittercode.model.Book;
import com.bittercode.model.StoreException;
import com.bittercode.service.BookService;
import com.bittercode.util.DBUtil;

/**
 * Implementation of BookService interface
 * Refactored to improve exception handling and resource management
 */
public class BookServiceImpl implements BookService {
    private static final Logger logger = Logger.getLogger(BookServiceImpl.class.getName());

    private static final String getAllBooksQuery = "SELECT * FROM " + BooksDBConstants.TABLE_BOOK;
    private static final String getBookByIdQuery = "SELECT * FROM " + BooksDBConstants.TABLE_BOOK
            + " WHERE " + BooksDBConstants.COLUMN_BARCODE + " = ?";

    private static final String deleteBookByIdQuery = "DELETE FROM " + BooksDBConstants.TABLE_BOOK + "  WHERE "
            + BooksDBConstants.COLUMN_BARCODE + "=?";

    private static final String addBookQuery = "INSERT INTO " + BooksDBConstants.TABLE_BOOK + "  VALUES(?,?,?,?,?)";

    private static final String updateBookQtyByIdQuery = "UPDATE " + BooksDBConstants.TABLE_BOOK + " SET "
            + BooksDBConstants.COLUMN_QUANTITY + "=? WHERE " + BooksDBConstants.COLUMN_BARCODE
            + "=?";

    private static final String updateBookByIdQuery = "UPDATE " + BooksDBConstants.TABLE_BOOK + " SET "
            + BooksDBConstants.COLUMN_NAME + "=? , "
            + BooksDBConstants.COLUMN_AUTHOR + "=?, "
            + BooksDBConstants.COLUMN_PRICE + "=?, "
            + BooksDBConstants.COLUMN_QUANTITY + "=? "
            + "  WHERE " + BooksDBConstants.COLUMN_BARCODE
            + "=?";

    @Override
    public Book getBookById(String bookId) throws StoreException {
        Book book = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(getBookByIdQuery);
            ps.setString(1, bookId);
            rs = ps.executeQuery();

            if (rs.next()) {
                String bCode = rs.getString(1);
                String bName = rs.getString(2);
                String bAuthor = rs.getString(3);
                int bPrice = rs.getInt(4);
                int bQty = rs.getInt(5);

                book = new Book(bCode, bName, bAuthor, bPrice, bQty);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving book with ID: " + bookId, e);
            throw new StoreException("Error retrieving book: " + e.getMessage());
        } finally {
            DBUtil.closeResources(null, ps, rs);
        }
        return book;
    }

    @Override
    public List<Book> getAllBooks() throws StoreException {
        List<Book> books = new ArrayList<Book>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(getAllBooksQuery);
            rs = ps.executeQuery();

            while (rs.next()) {
                String bCode = rs.getString(1);
                String bName = rs.getString(2);
                String bAuthor = rs.getString(3);
                int bPrice = rs.getInt(4);
                int bQty = rs.getInt(5);

                Book book = new Book(bCode, bName, bAuthor, bPrice, bQty);
                books.add(book);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving all books", e);
            throw new StoreException("Error retrieving books: " + e.getMessage());
        } finally {
            DBUtil.closeResources(null, ps, rs);
        }
        return books;
    }

    @Override
    public String deleteBookById(String bookId) throws StoreException {
        String response = ResponseCode.FAILURE.name();
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(deleteBookByIdQuery);
            ps.setString(1, bookId);
            int k = ps.executeUpdate();
            if (k == 1) {
                response = ResponseCode.SUCCESS.name();
                logger.info("Book deleted successfully: " + bookId);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting book with ID: " + bookId, e);
            response += " : " + e.getMessage();
        } finally {
            DBUtil.closeResources(null, ps, null);
        }
        return response;
    }

    @Override
    public String addBook(Book book) throws StoreException {
        String responseCode = ResponseCode.FAILURE.name();
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(addBookQuery);
            ps.setString(1, book.getBarcode());
            ps.setString(2, book.getName());
            ps.setString(3, book.getAuthor());
            ps.setDouble(4, book.getPrice());
            ps.setInt(5, book.getQuantity());
            int k = ps.executeUpdate();
            if (k == 1) {
                responseCode = ResponseCode.SUCCESS.name();
                logger.info("Book added successfully: " + book.getBarcode());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding book: " + book.getBarcode(), e);
            responseCode += " : " + e.getMessage();
        } finally {
            DBUtil.closeResources(null, ps, null);
        }
        return responseCode;
    }

    @Override
    public String updateBookQtyById(String bookId, int quantity) throws StoreException {
        String responseCode = ResponseCode.FAILURE.name();
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(updateBookQtyByIdQuery);
            ps.setInt(1, quantity);
            ps.setString(2, bookId);
            int k = ps.executeUpdate();
            if (k == 1) {
                responseCode = ResponseCode.SUCCESS.name();
                logger.info("Book quantity updated successfully: " + bookId + ", new quantity: " + quantity);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating book quantity for ID: " + bookId, e);
            responseCode += " : " + e.getMessage();
        } finally {
            DBUtil.closeResources(null, ps, null);
        }
        return responseCode;
    }

    @Override
    public List<Book> getBooksByCommaSeperatedBookIds(String commaSeperatedBookIds) throws StoreException {
        List<Book> books = new ArrayList<Book>();
        
        if (commaSeperatedBookIds == null || commaSeperatedBookIds.trim().isEmpty()) {
            return books;
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DBUtil.getConnection();
            
            // Create a parameterized query with placeholders for each book ID
            String[] bookIds = commaSeperatedBookIds.split(",");
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < bookIds.length; i++) {
                placeholders.append(i > 0 ? ", ?" : "?");
            }
            
            String query = "SELECT * FROM " + BooksDBConstants.TABLE_BOOK +
                    " WHERE " + BooksDBConstants.COLUMN_BARCODE + " IN (" + placeholders + ")";
            
            ps = con.prepareStatement(query);
            
            // Set each book ID as a parameter
            for (int i = 0; i < bookIds.length; i++) {
                ps.setString(i + 1, bookIds[i].trim());
            }
            
            rs = ps.executeQuery();

            while (rs.next()) {
                String bCode = rs.getString(1);
                String bName = rs.getString(2);
                String bAuthor = rs.getString(3);
                int bPrice = rs.getInt(4);
                int bQty = rs.getInt(5);

                Book book = new Book(bCode, bName, bAuthor, bPrice, bQty);
                books.add(book);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving books by IDs: " + commaSeperatedBookIds, e);
            throw new StoreException("Error retrieving books: " + e.getMessage());
        } finally {
            DBUtil.closeResources(null, ps, rs);
        }
        return books;
    }

    @Override
    public String updateBook(Book book) throws StoreException {
        String responseCode = ResponseCode.FAILURE.name();
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(updateBookByIdQuery);
            ps.setString(1, book.getName());
            ps.setString(2, book.getAuthor());
            ps.setDouble(3, book.getPrice());
            ps.setInt(4, book.getQuantity());
            ps.setString(5, book.getBarcode());
            int k = ps.executeUpdate();
            if (k == 1) {
                responseCode = ResponseCode.SUCCESS.name();
                logger.info("Book updated successfully: " + book.getBarcode());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating book: " + book.getBarcode(), e);
            responseCode += " : " + e.getMessage();
        } finally {
            DBUtil.closeResources(null, ps, null);
        }
        return responseCode;
    }
}
