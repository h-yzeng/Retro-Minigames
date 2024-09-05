package com.retro;

import javax.swing.*;
import java.awt.*;

/**
 * LoginScreen class represents the login interface for users to sign in or register.
 */
public class LoginScreen extends JFrame {
    private UserService userService; // Service for user-related operations

    /**
     * Constructs a new LoginScreen and sets up its components.
     */
    public LoginScreen() {
        // Initialize UserService
        userService = new UserService();

        // Frame settings
        setTitle("Retro Games Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        // Create and set up GUI components
        initializeComponents();
    }

    /**
     * Initializes GUI components and sets up layout and event listeners.
     */
    private void initializeComponents() {
        // Create components
        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField(20);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passText = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        // Set up layout manager and add components to panel
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(userLabel);
        panel.add(userText);
        panel.add(passLabel);
        panel.add(passText);
        panel.add(loginButton);
        panel.add(registerButton);
        add(panel);

        // Attach event listeners to buttons
        loginButton.addActionListener(e -> handleLogin(userText, passText));
        registerButton.addActionListener(e -> handleRegister(userText, passText));
    }

    /**
     * Handles the login action when the login button is clicked.
     *
     * @param userText JTextField for username input
     * @param passText JPasswordField for password input
     */
    private void handleLogin(JTextField userText, JPasswordField passText) {
        String username = userText.getText();
        String password = new String(passText.getPassword());

        if (!validateInput(username, password)) {
            return;
        }

        // Create a User object with the username
        User user = new User(username, null); // Password not needed in the User object for authentication

        // Authenticate user using UserService with plain password
        if (userService.authenticateUser(user, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new DashboardScreen(username).setVisible(true); // Open the DashboardScreen
            this.dispose(); // Close the LoginScreen
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the registration action when the register button is clicked.
     *
     * @param userText JTextField for username input
     * @param passText JPasswordField for password input
     */
    private void handleRegister(JTextField userText, JPasswordField passText) {
        String username = userText.getText();
        String password = new String(passText.getPassword());

        if (!validateInput(username, password)) {
            return;
        }

        // Create a User object
        User user = new User(username, null);

        // Register user using UserService
        if (userService.registerUser(user, password)) {
            JOptionPane.showMessageDialog(this, "User registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed! Username might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validates user input for both username and password.
     *
     * @param username the entered username
     * @param password the entered password
     * @return true if both username and password are non-empty; false otherwise
     */
    private boolean validateInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * The main method to launch the LoginScreen.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginScreen login = new LoginScreen();
            login.setVisible(true);
        });
    }
}
