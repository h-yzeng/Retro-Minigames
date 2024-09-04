package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardScreen extends JFrame {

    public DashboardScreen(String username) {
        // Frame settings
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
        playTicTacToeButton.addActionListener(e -> new TicTacToeGame());
        playSnakeButton.addActionListener(e -> new SnakeGame());
        playPongButton.addActionListener(e -> new PongGame());
        profileButton.addActionListener(e -> new UserProfileScreen(username));
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginScreen().setVisible(true);
        });
    }
}
