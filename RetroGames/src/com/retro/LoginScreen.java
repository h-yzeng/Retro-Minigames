package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt; // Import BCrypt for hashing passwords

import com.retro.User;
import com.retro.UserService; // Import User and UserService classes

public class LoginScreen extends JFrame {
    private UserService userService; // Declare a UserService instance

    // Constructor to set up GUI components
    public LoginScreen() {
        // Initialize UserService
        userService = new UserService();

        // Frame settings
        setTitle("Retro Games Login");
        setSize(300, 200);
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
        panel.setLayout(new GridLayout(4, 2));
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

            // Create a User object
            User user = new User(username, password);

            // Authenticate user using UserService
            if (userService.authenticateUser(user)) {
                JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Open the DashboardScreen and pass the username
                new DashboardScreen(username).setVisible(true);
                this.dispose(); // Close the LoginScreen
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create a User object
            User user = new User(username, password);

            // Register user using UserService
            if (userService.registerUser(user)) {
                JOptionPane.showMessageDialog(null, "User registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Registration failed! Username might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
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
