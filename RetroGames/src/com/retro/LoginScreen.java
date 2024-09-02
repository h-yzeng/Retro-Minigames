package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt; // Import BCrypt for hashing passwords

public class LoginScreen extends JFrame {

    // Constructor to set up GUI components
    public LoginScreen() {
        // Frame settings
        setTitle("Retro Games Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        
        // Create components
        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField(20);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passText = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        
        // Add components to frame
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(userLabel);
        panel.add(userText);
        panel.add(passLabel);
        panel.add(passText);
        panel.add(loginButton);
        panel.add(registerButton);
        
        add(panel);
        
        // Action listeners for buttons
        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Use DatabaseManager to connect to the database
            try (Connection conn = DatabaseManager.connect()) {
                if (conn != null) {
                    // Prepare SQL query to find the user
                    String query = "SELECT password FROM users WHERE username = ?";
                    PreparedStatement preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, username);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        String storedHash = resultSet.getString("password");

                        if (BCrypt.checkpw(password, storedHash)) {
                            JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            // TODO: Redirect to the next screen or perform another action
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }

                    // Close the ResultSet and PreparedStatement
                    resultSet.close();
                    preparedStatement.close();

                } else {
                    System.out.println("Failed to connect to the database.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to connect to the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Use DatabaseManager to connect to the database
            try (Connection conn = DatabaseManager.connect()) {
                if (conn != null) {
                    // Hash the password before storing it
                    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                    // Prepare SQL query to insert a new user
                    String query = "INSERT INTO users (username, password) VALUES (?, ?)";
                    PreparedStatement preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, hashedPassword);

                    int rowsInserted = preparedStatement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "User registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Close the PreparedStatement
                    preparedStatement.close();

                } else {
                    System.out.println("Failed to connect to the database.");
                }
            } catch (SQLException ex) {
                if (ex.getSQLState().equals("23000")) { // Duplicate entry error code
                    JOptionPane.showMessageDialog(null, "Username already exists. Please choose another username.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to connect to the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginScreen login = new LoginScreen();
            login.setVisible(true);
        });
    }
}
