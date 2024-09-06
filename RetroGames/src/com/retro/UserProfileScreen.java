package com.retro;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

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
        setSize(400, 400); // Increased size to accommodate more information
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        // Fetch user ID based on username
        this.userId = fetchUserIdByUsername(username);

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
        fetchPersonalBestScores(statsPanel); // Fetch personal best scores

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

    /**
     * Fetches the personal best scores for each game and displays them on the profile.
     */
    private void fetchPersonalBestScores(JPanel statsPanel) {
        try (Connection conn = DatabaseManager.connect()) {
            String query = "SELECT game_name, MAX(high_score) AS best_score FROM highscores WHERE user_id = ? GROUP BY game_name";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId); // Assuming userId is set when the user logs in
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String gameName = rs.getString("game_name");
                        int bestScore = rs.getInt("best_score");
                        statsPanel.add(new JLabel(gameName + " Personal Best: " + bestScore));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches the user ID from the database based on the username.
     * @return The user ID if found; -1 otherwise.
     */
    private int fetchUserIdByUsername(String username) {
        int userId = -1;
        try (Connection conn = DatabaseManager.connect()) {
            if (conn != null) {
                String query = "SELECT id FROM users WHERE username = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, username);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            userId = rs.getInt("id");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userId;
    }

    /**
     * Updates the user profile with new username and password.
     */
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

    /**
     * Navigates back to the Dashboard screen.
     */
    private void goBack() {
        this.dispose();
        new DashboardScreen(currentUsername).setVisible(true);
    }
}
