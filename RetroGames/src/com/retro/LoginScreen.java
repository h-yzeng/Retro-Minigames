package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

            Connection conn = connect();

            if (conn != null) {
                try {
                    // Prepare SQL query to find the user
                    String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                    PreparedStatement preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        // User exists and credentials are correct
                        JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // TODO: Redirect to the next screen or perform another action
                    } else {
                        // User does not exist or credentials are incorrect
                        JOptionPane.showMessageDialog(null, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                    
                    // Close the ResultSet and PreparedStatement
                    resultSet.close();
                    preparedStatement.close();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        conn.close(); // Close the connection after use
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                System.out.println("Failed to connect to the database.");
            }
        });

        registerButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            Connection conn = connect();

            if (conn != null) {
                try {
                    // Prepare SQL query to insert a new user
                    String query = "INSERT INTO users (username, password) VALUES (?, ?)";
                    PreparedStatement preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);

                    int rowsInserted = preparedStatement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "User registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Close the PreparedStatement
                    preparedStatement.close();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        conn.close(); // Close the connection after use
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                System.out.println("Failed to connect to the database.");
            }
        });

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginScreen login = new LoginScreen();
            login.setVisible(true);
        });
    }

    // Method to establish a connection to the database
    private Connection connect() {
        String url = "jdbc:mysql://localhost:3306/retro_games_db";
        String user = "root"; // Replace with your MySQL username
        String password = "Hchen43859hY!"; // Replace with your MySQL password

        try {
            System.out.println("Attempting to connect to the database..."); // Debug message
            // Establish a connection to the database
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully!"); // Success message
            return conn;
        } catch (SQLException e) {
            // Print stack trace in case of an SQL exception
            e.printStackTrace();
            // Show a dialog box in case of an error
            JOptionPane.showMessageDialog(null, "Failed to connect to the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
