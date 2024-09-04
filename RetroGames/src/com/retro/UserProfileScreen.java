package com.retro;

import javax.swing.*;
import org.mindrot.jbcrypt.BCrypt;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserProfileScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton updateButton;
    private JButton backButton;
    private String currentUsername;

    public UserProfileScreen(String username) {
        this.currentUsername = username;

        setTitle("User Profile");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        // Create components
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(username, 20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        updateButton = new JButton("Update Profile");
        backButton = new JButton("Back");

        // Add components to frame
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(updateButton);
        panel.add(backButton);
        add(panel);

        // Action listeners for buttons
        updateButton.addActionListener(e -> updateProfile());
        backButton.addActionListener(e -> goBack());

        setVisible(true);
    }

    private void updateProfile() {
        String newUsername = usernameField.getText();
        String newPassword = new String(passwordField.getPassword());

        if (newUsername.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = DatabaseManager.connect();

        if (conn != null) {
            try {
                String updateQuery = "UPDATE users SET username = ?, password = ? WHERE username = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
                preparedStatement.setString(1, newUsername);
                preparedStatement.setString(2, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
                preparedStatement.setString(3, currentUsername);

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(null, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    currentUsername = newUsername; // Update current username
                } else {
                    JOptionPane.showMessageDialog(null, "Profile update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                preparedStatement.close();
                conn.close();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void goBack() {
        this.dispose();
        new DashboardScreen(currentUsername).setVisible(true);
    }
}
