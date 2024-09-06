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
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setResizable(false); // Disable resizing to maintain layout

        // Create and set up GUI components
        initializeComponents();
    }

    /**
     * Initializes GUI components and sets up layout and event listeners.
     */
    private void initializeComponents() {
        // Set up main panel with padding and grid bag layout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // This will make components fill the horizontal space
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components

        // Create components with enhanced styles
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField userText = new JTextField(20);
        userText.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JPasswordField passText = new JPasswordField(20);
        passText.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        // Style buttons
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(70, 130, 180)); // Steel Blue
        loginButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(34, 139, 34)); // Forest Green
        registerButton.setForeground(Color.WHITE);

        // Add components to the panel with GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0; // No horizontal weight for labels
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // Allow text fields to grow horizontally
        panel.add(userText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0; // No horizontal weight for labels
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // Allow text fields to grow horizontally
        panel.add(passText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0; // No horizontal weight for buttons
        panel.add(loginButton, gbc);

        gbc.gridy = 3;
        panel.add(registerButton, gbc);

        add(panel);

        // Attach event listeners to buttons
        loginButton.addActionListener(e -> handleLogin(userText, passText));
        registerButton.addActionListener(e -> handleRegister(userText, passText));
    }


    /**
     * Handles the login action when the login button is clicked.
     */
    private void handleLogin(JTextField userText, JPasswordField passText) {
        String username = userText.getText();
        String plainPassword = new String(passText.getPassword());

        if (!validateInput(username, plainPassword)) {
            return;
        }

        // Authenticate user using UserService
        User user = new User(username, null); // Password is not stored in the user object
        if (userService.authenticateUser(user, plainPassword)) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new DashboardScreen(username).setVisible(true); // Open the DashboardScreen
            this.dispose(); // Close the LoginScreen
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * Handles the registration action when the register button is clicked.
     */
    private void handleRegister(JTextField userText, JPasswordField passText) {
        String username = userText.getText();
        String plainPassword = new String(passText.getPassword());

        if (!validateInput(username, plainPassword)) {
            return;
        }

        // Register user using UserService
        User user = new User(username, null); // Password is not stored in the user object
        if (userService.registerUser(user, plainPassword)) {
            JOptionPane.showMessageDialog(this, "User registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed! Username might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validates user input for both username and password.
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