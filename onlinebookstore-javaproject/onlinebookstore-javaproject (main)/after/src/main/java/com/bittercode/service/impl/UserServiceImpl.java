package com.bittercode.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import com.bittercode.constant.ResponseCode;
import com.bittercode.constant.db.UsersDBConstants;
import com.bittercode.model.StoreException;
import com.bittercode.model.User;
import com.bittercode.model.UserRole;
import com.bittercode.service.UserService;
import com.bittercode.util.DBUtil;

/**
 * Implementation of UserService interface
 * Refactored to improve exception handling and resource management
 */
public class UserServiceImpl implements UserService {
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    private static final String registerUserQuery = "INSERT INTO " + UsersDBConstants.TABLE_USERS
            + "  VALUES(?,?,?,?,?,?,?,?)";

    private static final String loginUserQuery = "SELECT * FROM " + UsersDBConstants.TABLE_USERS + " WHERE "
            + UsersDBConstants.COLUMN_USERNAME + "=? AND " + UsersDBConstants.COLUMN_PASSWORD + "=? AND "
            + UsersDBConstants.COLUMN_USERTYPE + "=?";

    @Override
    public User login(UserRole role, String email, String password, HttpSession session) throws StoreException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;
        
        try {
            con = DBUtil.getConnection();
            String userType = UserRole.SELLER.equals(role) ? "1" : "2";
            ps = con.prepareStatement(loginUserQuery);
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, userType);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                user = new User();
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                user.setPhone(rs.getLong("phone"));
                user.setEmailId(email);
                user.setPassword(password);
                session.setAttribute(role.toString(), user.getEmailId());
                logger.info("User logged in successfully: " + email + ", role: " + role);
            } else {
                logger.warning("Login failed for user: " + email + ", role: " + role);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error during login for user: " + email, e);
            throw new StoreException("Error during login: " + e.getMessage());
        } finally {
            DBUtil.closeResources(null, ps, rs);
        }
        return user;
    }

    @Override
    public boolean isLoggedIn(UserRole role, HttpSession session) {
        if (role == null) {
            role = UserRole.CUSTOMER;
        }
        boolean loggedIn = session != null && session.getAttribute(role.toString()) != null;
        if (loggedIn) {
            logger.fine("User is logged in with role: " + role);
        }
        return loggedIn;
    }

    @Override
    public boolean logout(HttpSession session) {
        if (session != null) {
            try {
                String customerEmail = (String) session.getAttribute(UserRole.CUSTOMER.toString());
                String sellerEmail = (String) session.getAttribute(UserRole.SELLER.toString());
                
                if (customerEmail != null) {
                    logger.info("Customer logged out: " + customerEmail);
                }
                if (sellerEmail != null) {
                    logger.info("Seller logged out: " + sellerEmail);
                }
                
                session.removeAttribute(UserRole.CUSTOMER.toString());
                session.removeAttribute(UserRole.SELLER.toString());
                session.invalidate();
                return true;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error during logout", e);
                return false;
            }
        }
        return false;
    }

    @Override
    public String register(UserRole role, User user) throws StoreException {
        String responseMessage = ResponseCode.FAILURE.name();
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(registerUserQuery);
            ps.setString(1, user.getEmailId());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, user.getAddress());
            ps.setLong(6, user.getPhone());
            ps.setString(7, user.getEmailId());
            int userType = UserRole.SELLER.equals(role) ? 1 : 2;
            ps.setInt(8, userType);
            
            int k = ps.executeUpdate();
            if (k == 1) {
                responseMessage = ResponseCode.SUCCESS.name();
                logger.info("User registered successfully: " + user.getEmailId() + ", role: " + role);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during user registration: " + user.getEmailId(), e);
            responseMessage += " : " + e.getMessage();
            if (e.getMessage() != null && e.getMessage().contains("Duplicate")) {
                responseMessage = "User already registered with this email !!";
            }
        } finally {
            DBUtil.closeResources(null, ps, null);
        }
        return responseMessage;
    }
}
