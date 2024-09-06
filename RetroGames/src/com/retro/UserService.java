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

    // Method to register a new user
    public boolean registerUser(User user, String plainPassword) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "INSERT INTO users (username, password) VALUES (?, ?)")) {

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

    // Method to authenticate a user and retrieve their ID
    public boolean authenticateUser(User user, String plainPassword) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "SELECT id, password FROM users WHERE username = ?")) {

            preparedStatement.setString(1, user.getUsername());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password");
                    int userId = resultSet.getInt("id");

                    // Check if the plain password matches the stored hash
                    if (BCrypt.checkpw(plainPassword, storedHash)) {
                        // Set the user's ID and mark them as logged in
                        user.setId(userId);
                        loggedInUser = user;
                        return true;
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }
    
    public static void logoutUser() {
        loggedInUser = null;
    }

    public static boolean isUserLoggedIn() {
        return loggedInUser != null;
    }
}
