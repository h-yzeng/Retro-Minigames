package com.retro;

import javax.swing.*;
import java.awt.*;

/**
 * DashboardScreen class represents the main dashboard after a user logs in.
 */
public class DashboardScreen extends JFrame {

    private String username; // Store the username for reuse

    public DashboardScreen(String username) {
        this.username = username; // Initialize username

        setTitle("Welcome " + username);
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        // Create components
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        JButton playTicTacToeButton = new JButton("Play Tic-Tac-Toe");
        JButton playSnakeButton = new JButton("Play Snake");
        JButton playPongButton = new JButton("Play Pong");
        JButton profileButton = new JButton("Profile");
        JButton logoutButton = new JButton("Logout");

        // Add components to frame
        setLayout(new GridLayout(6, 1));
        add(welcomeLabel);
        add(playTicTacToeButton);
        add(playSnakeButton);
        add(playPongButton);
        add(profileButton);
        add(logoutButton);

        // Action listeners for buttons
        playTicTacToeButton.addActionListener(e -> {
            if (UserService.isUserLoggedIn()) {
                new TicTacToeGame(username); // Pass username to constructor
            } else {
                showLoginError();
            }
        });

        playSnakeButton.addActionListener(e -> {
            if (UserService.isUserLoggedIn()) {
                new SnakeGame(username); // Pass username to constructor
            } else {
                showLoginError();
            }
        });

        playPongButton.addActionListener(e -> {
            if (UserService.isUserLoggedIn()) {
                new PongGame(username); // Pass username to constructor
            } else {
                showLoginError();
            }
        });

        profileButton.addActionListener(e -> {
            if (UserService.isUserLoggedIn()) {
                new UserProfileScreen(username); // Open UserProfileScreen with the username
            } else {
                showLoginError();
            }
        });

        logoutButton.addActionListener(e -> handleLogout());

        setVisible(true);
    }

    /**
     * Shows an error message if the user is not logged in.
     */
    private void showLoginError() {
        JOptionPane.showMessageDialog(this, "You must be logged in to access this feature.", "Access Denied", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Handles the logout action.
     */
    private void handleLogout() {
        UserService.logoutUser();
        this.dispose();
        new LoginScreen().setVisible(true);
    }
}
