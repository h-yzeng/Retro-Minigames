package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * SnakeGame class represents the Snake game UI and logic.
 */
public class SnakeGame extends JFrame {

    private static final int WINDOW_WIDTH = 300;
    private static final int WINDOW_HEIGHT = 300;
    private static final int UNIT_SIZE = 10;
    private static final int GAME_UNITS = (WINDOW_WIDTH * WINDOW_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 75; // Timer delay in milliseconds

    private final int[] x = new int[GAME_UNITS]; // x-coordinates of snake's body
    private final int[] y = new int[GAME_UNITS]; // y-coordinates of snake's body
    private int bodyParts = 6; // Initial length of snake
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R'; // Snake starts moving to the right
    private boolean running = false;
    private Timer timer;
    private String username;
    private int userId;

    private SoundManager soundManager; // SoundManager to play sound effects
    private DashboardScreen dashboardScreen; // Reference to the existing DashboardScreen

    /**
     * Constructs a new SnakeGame window.
     */
    public SnakeGame(String username, DashboardScreen dashboardScreen) {
        if (!UserService.isUserLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Access denied! You must be logged in to play.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            this.dispose();
            return;
        }

        this.username = username;
        this.userId = fetchUserIdByUsername(username); // Correctly fetch user ID from database
        this.soundManager = new SoundManager(); // Initialize sound manager
        this.dashboardScreen = dashboardScreen;  // Store the reference to the existing DashboardScreen

        setTitle("Snake Game");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setResizable(false);

        initGamePanel();
        startGame();
    }

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

    private void initGamePanel() {
        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw(g);
            }
        };
        gamePanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(new MyKeyAdapter());
        add(gamePanel);
        pack();
        setVisible(true);
    }

    private void startGame() {
        // Initialize the snake's starting position
        for (int i = 0; i < bodyParts; i++) {
            x[i] = UNIT_SIZE * (bodyParts - i - 1);
            y[i] = 0;
        }

        newApple();
        running = true;
        timer = new Timer(DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) {
                    move();
                    checkApple();
                    checkCollisions();
                }
                repaint();
            }
        });
        timer.start();
    }

    private void newApple() {
        appleX = (int) (Math.random() * (WINDOW_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = (int) (Math.random() * (WINDOW_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    private void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            SoundManager.playSound("assets/sounds/points_score.wav"); // Play sound when apple is eaten
            newApple();
        }
    }

    private void checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }

        // Check if head collides with walls
        if (x[0] < 0 || x[0] >= WINDOW_WIDTH || y[0] < 0 || y[0] >= WINDOW_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
            gameOver();
        }
    }

    private void draw(Graphics g) {
        if (running) {
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        } else {
            gameOver(g);
        }
    }

    private void gameOver() {
        int highScore = applesEaten;
        SoundManager.playSound("assets/sounds/game_over.wav"); // Play game over sound
        updateStatistics(highScore);

        String message = "Game Over. Your score: " + highScore + "\nDo you want to play again?";
        int response = JOptionPane.showConfirmDialog(
            this,
            message,
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            restartGame(); // Restart the game
        } else {
            this.dispose(); // Close the current game window
            if (dashboardScreen != null) {
                dashboardScreen.setVisible(true); // Show the existing DashboardScreen
            }
        }
    }

    private void updateStatistics(int highScore) {
        if (userId != -1) {
            DatabaseManager.updateUserStatistics(userId, "Snake", false, highScore);
        } else {
            JOptionPane.showMessageDialog(null, "Error updating statistics. User not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gameOver(Graphics g) {
        String message = "Game Over";
        Font font = new Font("Helvetica", Font.BOLD, 20);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.RED);
        g.setFont(font);
        g.drawString(message, (WINDOW_WIDTH - metrics.stringWidth(message)) / 2, WINDOW_HEIGHT / 2);
        
        // Save the score if it's a high score
        saveHighScore("Snake", applesEaten);
    }

    // Method to save high score in the database
    private void saveHighScore(String gameName, int score) {
        if (UserService.isUserLoggedIn()) {
            User user = UserService.getLoggedInUser();
            try (Connection conn = DatabaseManager.connect()) {
                String query = "INSERT INTO highscores (user_id, game_name, high_score) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, user.getId());
                    stmt.setString(2, gameName);
                    stmt.setInt(3, score);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void restartGame() {
        // Reset game variables
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = true;

        // Reset the snake's position to the center
        int centerX = WINDOW_WIDTH / 2;
        int centerY = WINDOW_HEIGHT / 2;
        for (int i = 0; i < bodyParts; i++) {
            x[i] = centerX - i * UNIT_SIZE;
            y[i] = centerY;
        }

        // Generate a new apple
        newApple();

        // Restart the timer
        timer.start();

        // Repaint the game panel to reflect the reset state
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W: // Move Up
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_S: // Move Down
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_A: // Move Left
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_D: // Move Right
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
            }
        }
    }
}