package com.retro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    // Method to register a new user
    public boolean registerUser(User user) {
        try (Connection conn = DatabaseManager.connect()) {
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, hashedPassword);

            int rowsInserted = preparedStatement.executeUpdate();
            preparedStatement.close();

            return rowsInserted > 0;
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23000")) {
                System.out.println("Username already exists. Please choose another username.");
            } else {
                ex.printStackTrace();
            }
            return false;
        }
    }

    // Method to authenticate a user
    public boolean authenticateUser(User user) {
        try (Connection conn = DatabaseManager.connect()) {
            String query = "SELECT password FROM users WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, user.getUsername());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedHash = resultSet.getString("password");
                resultSet.close();
                preparedStatement.close();

                return BCrypt.checkpw(user.getPassword(), storedHash);
            }

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
