package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * DashboardScreen class represents the main dashboard after a user logs in.
 */
public class DashboardScreen extends JFrame {

    private String username; // Store the username for reuse

    public DashboardScreen(String username) {
        this.username = username; // Initialize username

        // Frame settings
        setTitle("Welcome " + username);
        setSize(450, 400); // Slightly larger to fit new styles
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setResizable(false);  // Disable resizing to maintain layout

        // Create components
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(0, 0, 0));  // Black color for welcome message

        // Create game buttons with modern styles and icons
        JButton playTicTacToeButton = createStyledButton("Play Tic-Tac-Toe", "");
        JButton playSnakeButton = createStyledButton("Play Snake", "");
        JButton playPongButton = createStyledButton("Play Pong", "");

        // Profile and Logout buttons with distinct styles
        JButton profileButton = createProfileLogoutButton("Profile");
        JButton logoutButton = createProfileLogoutButton("Logout");

        // Layout for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));  // 5 rows, vertical gap of 10px
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));  // Padding around the panel

        // Add buttons to panel
        buttonPanel.add(playTicTacToeButton);
        buttonPanel.add(playSnakeButton);
        buttonPanel.add(playPongButton);
        buttonPanel.add(profileButton);
        buttonPanel.add(logoutButton);

        // Set layout and add components to the frame
        setLayout(new BorderLayout());
        add(welcomeLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        // Play background music when the dashboard loads
        SoundManager.playBackgroundMusic("assets/sounds/background_music.wav");

        // Add sound for button clicks and stop background music when switching screens
        playTicTacToeButton.addActionListener(e -> {
            SoundManager.playSound("assets/sounds/button_click.wav");
            SoundManager.stopBackgroundMusic(); // Stop background music
            if (UserService.isUserLoggedIn()) {
                new TicTacToeGame(username);
            } else {
                showLoginError();
            }
        });

        playSnakeButton.addActionListener(e -> {
            SoundManager.playSound("assets/sounds/button_click.wav");
            SoundManager.stopBackgroundMusic(); // Stop background music
            if (UserService.isUserLoggedIn()) {
                new SnakeGame(username, this);  // Pass 'this' for DashboardScreen reference
            } else {
                showLoginError();
            }
        });

        playPongButton.addActionListener(e -> {
            SoundManager.playSound("assets/sounds/button_click.wav");
            SoundManager.stopBackgroundMusic(); // Stop background music
            if (UserService.isUserLoggedIn()) {
                new PongGame(username);
            } else {
                showLoginError();
            }
        });

        profileButton.addActionListener(e -> {
            SoundManager.playSound("assets/sounds/button_click.wav");
            SoundManager.stopBackgroundMusic(); // Stop background music
            if (UserService.isUserLoggedIn()) {
                new UserProfileScreen(username, this);
            } else {
                showLoginError();
            }
        });

        logoutButton.addActionListener(e -> {
            SoundManager.playSound("assets/sounds/button_click.wav");
            SoundManager.stopBackgroundMusic(); // Stop background music
            handleLogout();
        });

        setVisible(true);
    }

    /**
     * Creates a modern styled button with a label and icon, including hover effect.
     */
    private JButton createStyledButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180));  // Steel blue color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);  // Remove focus border
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));  // Padding for button

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 149, 237));  // Lighter blue on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));  // Back to original color
            }
        });

        return button;
    }

    /**
     * Creates styled Profile/Logout buttons with hover effect.
     */
    private JButton createProfileLogoutButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(34, 139, 34));  // Forest Green color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);  // Remove focus border
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));  // Padding for button

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(50, 205, 50));  // Lighter green on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(34, 139, 34));  // Back to original color
            }
        });

        return button;
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