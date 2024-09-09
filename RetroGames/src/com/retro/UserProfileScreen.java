package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private DashboardScreen dashboardScreen;

    public UserProfileScreen(String username, DashboardScreen dashboardScreen) {
        this.currentUsername = username;
        this.dashboardScreen = dashboardScreen;

        // Frame settings
        setTitle("User Profile");
        setSize(500, 500); // Adjusted size for detailed stats
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setResizable(false);

        // Fetch user ID based on username
        this.userId = fetchUserIdByUsername(username);

        // Create components
        JLabel profileLabel = new JLabel("Profile Settings", SwingConstants.CENTER);
        profileLabel.setFont(new Font("Arial", Font.BOLD, 22));
        profileLabel.setForeground(new Color(0, 0, 0));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(username);
        usernameField.setPreferredSize(new Dimension(300, 40));

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 40));

        updateButton = createStyledButton("Update Profile");
        backButton = createStyledButton("Back");

        // Fetch and display user statistics
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(10, 1, 5, 5)); // Adjusted for detailed stats
        statsPanel.setBorder(BorderFactory.createTitledBorder("Detailed Game Statistics"));
        fetchUserStatistics(statsPanel); // Fetch detailed game stats

        // Add components to frame
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Allows horizontal expansion of text fields
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; // Reset weightx for other components
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Allows horizontal expansion of text fields
        formPanel.add(passwordField, gbc);

        // Button layout
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(updateButton, gbc);
        gbc.gridy = 3;
        formPanel.add(backButton, gbc);

        setLayout(new BorderLayout());
        add(profileLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        updateButton.addActionListener(e -> {
            SoundManager.playSound("assets/sounds/button_click.wav");
            updateProfile();
        });

        backButton.addActionListener(e -> {
            SoundManager.playSound("assets/sounds/button_click.wav");
            goBack();
        });

        setVisible(true);
    }

    /**
     * Creates a styled button with a modern look and hover effect.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180));  // Steel blue color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

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
     * Fetches the user's detailed game statistics and displays them in the profile.
     */
    private void fetchUserStatistics(JPanel statsPanel) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {

            stmt.setString(1, currentUsername);

            // Execute query and get ResultSet
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Fetch all necessary data before the ResultSet is closed
                    int ticTacToeGamesPlayed = rs.getInt("tic_tac_toe_games_played");
                    int ticTacToeGamesWon = rs.getInt("tic_tac_toe_games_won");
                    int ticTacToeGamesDrawn = rs.getInt("tic_tac_toe_games_drawn");
                    int snakeGamesPlayed = rs.getInt("snake_games_played");
                    int snakeHighScore = rs.getInt("snake_high_score");
                    int snakeApplesEaten = rs.getInt("snake_apples_eaten");
                    int pongGamesPlayed = rs.getInt("pong_games_played");
                    int pongGamesWon = rs.getInt("pong_games_won");
                    int pongTotalPoints = rs.getInt("pong_total_points");

                    // Populate the panel with the fetched data
                    statsPanel.add(new JLabel("Tic-Tac-Toe Games Played: " + ticTacToeGamesPlayed));
                    statsPanel.add(new JLabel("Tic-Tac-Toe Games Won: " + ticTacToeGamesWon));
                    statsPanel.add(new JLabel("Tic-Tac-Toe Games Drawn: " + ticTacToeGamesDrawn));
                    statsPanel.add(new JLabel("Snake Games Played: " + snakeGamesPlayed));
                    statsPanel.add(new JLabel("Snake High Score: " + snakeHighScore));
                    statsPanel.add(new JLabel("Snake Apples Eaten: " + snakeApplesEaten));
                    statsPanel.add(new JLabel("Pong Games Played: " + pongGamesPlayed));
                    statsPanel.add(new JLabel("Pong Games Won: " + pongGamesWon));
                    statsPanel.add(new JLabel("Pong Total Points: " + pongTotalPoints));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches the user ID from the database based on the username.
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
     * Navigates back to the original Dashboard screen.
     */
    private void goBack() {
        this.dispose();
        dashboardScreen.setVisible(true);
    }
}
