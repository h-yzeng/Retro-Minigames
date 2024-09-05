package com.retro;

import javax.swing.*;
import org.mindrot.jbcrypt.BCrypt;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * UserProfileScreen class represents the user profile management screen.
 */
public class UserProfileScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton updateButton;
    private JButton backButton;
    private String currentUsername;
    private int userId;

    public UserProfileScreen(String username) {
        this.currentUsername = username;

        setTitle("User Profile");
        setSize(300, 400); // Increased size to accommodate more information
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        // Create components
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(username, 20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        updateButton = new JButton("Update Profile");
        backButton = new JButton("Back");

        // Fetch and display user statistics
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(6, 1));
        fetchAndDisplayStatistics(statsPanel);

        // Add components to frame
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(updateButton);
        panel.add(backButton);
        add(panel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);

        // Action listeners for buttons
        updateButton.addActionListener(e -> updateProfile());
        backButton.addActionListener(e -> goBack());

        setVisible(true);
    }

    private void fetchAndDisplayStatistics(JPanel statsPanel) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {

            stmt.setString(1, currentUsername);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    this.userId = resultSet.getInt("id"); // Get user ID for further operations
                    statsPanel.add(new JLabel("Tic Tac Toe Games Played: " + resultSet.getInt("tic_tac_toe_games_played")));
                    statsPanel.add(new JLabel("Tic Tac Toe Games Won: " + resultSet.getInt("tic_tac_toe_games_won")));
                    statsPanel.add(new JLabel("Snake Games Played: " + resultSet.getInt("snake_games_played")));
                    statsPanel.add(new JLabel("Snake High Score: " + resultSet.getInt("snake_high_score")));
                    statsPanel.add(new JLabel("Pong Games Played: " + resultSet.getInt("pong_games_played")));
                    statsPanel.add(new JLabel("Pong Games Won: " + resultSet.getInt("pong_games_won")));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateProfile() {
        String newUsername = usernameField.getText();
        String newPassword = new String(passwordField.getPassword());

        if (newUsername.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseManager.connect()) {
            if (conn != null) {
                String updateQuery = "UPDATE users SET username = ?, password = ? WHERE id = ?";
                try (PreparedStatement preparedStatement = conn.prepareStatement(updateQuery)) {
                    preparedStatement.setString(1, newUsername);
                    preparedStatement.setString(2, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
                    preparedStatement.setInt(3, userId);
                    int rowsUpdated = preparedStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(null, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        currentUsername = newUsername;
                        UserService.getLoggedInUser().setUsername(newUsername);
                    } else {
                        JOptionPane.showMessageDialog(null, "Profile update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void goBack() {
        this.dispose();
        new DashboardScreen(currentUsername).setVisible(true);
    }
}
