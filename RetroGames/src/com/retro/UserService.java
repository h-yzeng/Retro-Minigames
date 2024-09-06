package com.retro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

/**
 * UserService class handles user registration and authentication logic.
 */
public class UserService {
    private static User loggedInUser = null; // Static variable to store the logged-in user

    /**
     * Registers a new user in the database.
     * @return true if the user is registered successfully; false otherwise
     */
    public boolean registerUser(User user, String plainPassword) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "INSERT INTO users (username, password) VALUES (?, ?)")) {

            // Hash the password before storing
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, hashedPassword);

            int rowsInserted = preparedStatement.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException ex) {
            if ("23000".equals(ex.getSQLState())) { // SQLState for unique constraint violation
                System.out.println("Username already exists. Please choose another username.");
            } else {
                ex.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Authenticates a user by checking the stored hash with the provided password.
     * @return true if the user is authenticated successfully; false otherwise
     */
    public boolean authenticateUser(User user, String plainPassword) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "SELECT password FROM users WHERE username = ?")) {

            preparedStatement.setString(1, user.getUsername());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password");

                    // Validate the plain password against the stored hash
                    if (BCrypt.checkpw(plainPassword, storedHash)) {
                        loggedInUser = user; // Store the logged-in user
                        return true;
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Logs out the current user.
     */
    public static void logoutUser() {
        loggedInUser = null; // Clear the logged-in user
    }

    /**
     * Checks if a user is currently logged in.
     * @return true if a user is logged in; false otherwise
     */
    public static boolean isUserLoggedIn() {
        return loggedInUser != null;
    }

    /**
     * Gets the currently logged-in user.
     * @return the currently logged-in User object, or null if no user is logged in
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }
}