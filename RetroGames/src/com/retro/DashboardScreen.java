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
        JButton playTicTacToeButton = new JButton("Play Tic-Tac-Toe");
        JButton playSnakeButton = new JButton("Play Snake");
        JButton logoutButton = new JButton("Logout");

        // Add components to frame
        setLayout(new GridLayout(4, 1));
        add(welcomeLabel);
        add(playTicTacToeButton);
        add(playSnakeButton);
        add(logoutButton);

        // Action listeners for buttons
        playTicTacToeButton.addActionListener(e -> {
            new TicTacToeGame(); // Open Tic-Tac-Toe game
        });

        playSnakeButton.addActionListener(e -> {
            new SnakeGame(); // Open Snake game
        });

        logoutButton.addActionListener(e -> {
            this.dispose(); // Close the DashboardScreen
            new LoginScreen().setVisible(true); // Show the LoginScreen again
        });
    }
}
