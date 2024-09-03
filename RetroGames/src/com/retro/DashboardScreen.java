package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardScreen extends JFrame {

    public DashboardScreen(String username) {
        // Frame settings
        setTitle("Welcome " + username);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        
        // Create components
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        JButton playGameButton = new JButton("Play Game");
        JButton logoutButton = new JButton("Logout");

        // Add components to frame
        setLayout(new BorderLayout());
        add(welcomeLabel, BorderLayout.NORTH);
        add(playGameButton, BorderLayout.CENTER);
        add(logoutButton, BorderLayout.SOUTH);

        // Action listeners for buttons
        playGameButton.addActionListener(e -> {
            // TODO: Open game selection screen or start a game
            JOptionPane.showMessageDialog(this, "Game feature coming soon!");
        });

        logoutButton.addActionListener(e -> {
            // Close the current frame and show the login screen again
            this.dispose(); // Close the DashboardScreen
            new LoginScreen().setVisible(true); // Show the LoginScreen again
        });
    }
}
