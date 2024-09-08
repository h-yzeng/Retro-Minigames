package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * PongGame class represents the Pong game UI and logic.
 */
public class PongGame extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 10;
    private static final int PADDLE_HEIGHT = 100;
    private static final int BALL_SIZE = 20;
    private static final int PADDLE_SPEED = 20;
    private static final int BALL_SPEED = 5;
    private static final int PADDLE_EDGE_OFFSET = 10;
    private static final int SCORE_LIMIT = 10; // Winning score limit

    private Timer gameTimer;
    private Paddle player1Paddle, player2Paddle;
    private Ball ball;
    private int player1Score, player2Score;
    private boolean isGameRunning = false; // Track if the game is running
    private String username;
    private int userId;

    /**
     * Constructs a new PongGame window and initializes the game components.
     */
    public PongGame(String username) {
        if (!UserService.isUserLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Access denied! You must be logged in to play.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            this.dispose();
            return;
        }

        this.username = username;
        this.userId = fetchUserIdByUsername(username); // Fetch the user ID from the database

        setTitle("Pong Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        initializeGameComponents();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        // Game timer to update game state
        gameTimer = new Timer(16, e -> updateGameState(gamePanel)); // Approx. 60 FPS
        gameTimer.start();

        setFocusable(true);
        setVisible(true);
        isGameRunning = true; // Game is now running
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
     * Initializes game components like paddles and the ball.
     */
    private void initializeGameComponents() {
        player1Paddle = new Paddle(PADDLE_EDGE_OFFSET, HEIGHT / 2 - PADDLE_HEIGHT / 2); // Left Paddle
        player2Paddle = new Paddle(WIDTH - PADDLE_EDGE_OFFSET - PADDLE_WIDTH, HEIGHT / 2 - PADDLE_HEIGHT / 2); // Right Paddle
        ball = new Ball(WIDTH / 2 - BALL_SIZE / 2, HEIGHT / 2 - BALL_SIZE / 2);

        player1Score = 0;
        player2Score = 0;
    }

    /**
     * Handles key press events for controlling the paddles.
     */
    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                if (player1Paddle.getY() > 0) player1Paddle.moveUp();
                break;
            case KeyEvent.VK_S:
                if (player1Paddle.getY() < HEIGHT - PADDLE_HEIGHT) player1Paddle.moveDown();
                break;
            case KeyEvent.VK_UP:
                if (player2Paddle.getY() > 0) player2Paddle.moveUp();
                break;
            case KeyEvent.VK_DOWN:
                if (player2Paddle.getY() < HEIGHT - PADDLE_HEIGHT) player2Paddle.moveDown();
                break;
        }
    }

    /**
     * Updates the game state including ball movement and collision checks.
     */
    private void updateGameState(GamePanel gamePanel) {
        if (!isGameRunning) return;

        ball.move();
        checkCollision();
        gamePanel.repaint();
    }

    /**
     * Checks for collisions with paddles, walls, and scoring conditions.
     */
    private void checkCollision() {
        // Ball collision with top and bottom borders
        if (ball.getY() <= 0 || ball.getY() >= HEIGHT - BALL_SIZE) {
            ball.reverseYDirection();
            // No sound when the ball hits the top or bottom walls
        }

        // Ball collision with left paddle
        if (ball.intersects(player1Paddle)) {
            ball.reverseXDirection();
            SoundManager.playSound("assets/sounds/hit_sound.wav"); // Play hit sound when ball hits a paddle
        }

        // Ball collision with right paddle
        if (ball.intersects(player2Paddle)) {
            ball.reverseXDirection();
            SoundManager.playSound("assets/sounds/hit_sound.wav"); // Play hit sound when ball hits a paddle
        }

        // Check if the ball goes out of bounds (scoring)
        if (ball.getX() <= 0) {
            player2Score++;
            SoundManager.playSound("assets/sounds/points_score.wav"); // Play sound when player scores
            resetBallDirectionTowardsPlayer(2); // Ball moves towards player 2
        } else if (ball.getX() >= WIDTH - BALL_SIZE) {
            player1Score++;
            SoundManager.playSound("assets/sounds/points_score.wav"); // Play sound when player scores
            resetBallDirectionTowardsPlayer(1); // Ball moves towards player 1
        }

        // Check if a player has reached the score limit
        if (player1Score >= SCORE_LIMIT) {
            SoundManager.playSound("assets/sounds/winning_sound.wav"); // Play winning sound for Player 1
            endGame("Player 1 Wins!");
        } else if (player2Score >= SCORE_LIMIT) {
            SoundManager.playSound("assets/sounds/winning_sound.wav"); // Play winning sound for Player 2
            endGame("Player 2 Wins!");
        }
    }
    
 // Method to reset the game after it ends or for a new round
    private void resetGame() {
        // Reset paddles
        player1Paddle.setPosition(PADDLE_EDGE_OFFSET, HEIGHT / 2 - PADDLE_HEIGHT / 2);
        player2Paddle.setPosition(WIDTH - PADDLE_EDGE_OFFSET - PADDLE_WIDTH, HEIGHT / 2 - PADDLE_HEIGHT / 2);

        // Reset ball
        ball.setPosition(WIDTH / 2 - BALL_SIZE / 2, HEIGHT / 2 - BALL_SIZE / 2);
        ball.setDirection(BALL_SPEED * (Math.random() < 0.5 ? 1 : -1), BALL_SPEED * (Math.random() < 0.5 ? 1 : -1));

        // Optionally reset scores if starting a new game
        player1Score = 0;
        player2Score = 0;

        // Restart the game timer
        isGameRunning = true;
        gameTimer.start();
    }


 // Game over method that takes the winner as a parameter
    private void gameOver(String winner) {
        JOptionPane.showMessageDialog(this, winner + " wins the game!");

        // Save high score based on the winning player
        if (winner.equals("Player 1")) {
            saveHighScore("Pong", player1Score);
        } else {
            saveHighScore("Pong", player2Score);
        }

        resetGame();
    }

    // Save high score to the database
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
    
    /**
     * Ends the game and shows a message.
     * @param winnerMessage the message to show indicating the winner
     */
    private void endGame(String winnerMessage) {
        isGameRunning = false;
        gameTimer.stop();
        JOptionPane.showMessageDialog(this, winnerMessage, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        updateStatistics(winnerMessage.contains("Player 1") ? true : false);

        // Ask if the player wants to play again
        int response = JOptionPane.showConfirmDialog(this, "Do you want to play again?", "Play Again", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            resetGame(); // Reset the game if the player wants to play again
        } else {
            dispose(); // Close the game window if the player does not want to play again
        }
    }


    /**
     * Resets the ball to the starting position after scoring, moving towards the specified player.
     * @param player the player number (1 or 2) towards whom the ball should move
     */
    private void resetBallDirectionTowardsPlayer(int player) {
        ball.setPosition(WIDTH / 2 - BALL_SIZE / 2, HEIGHT / 2 - BALL_SIZE / 2);
        int xDir = BALL_SPEED * (player == 1 ? 1 : -1);
        int yDir = BALL_SPEED * (Math.random() < 0.5 ? 1 : -1);
        ball.setDirection(xDir, yDir);
    }

    /**
     * Updates user statistics in the database.
     * @param player1Win true if Player 1 won, false otherwise
     */
    private void updateStatistics(boolean player1Win) {
        if (userId != -1) {
            DatabaseManager.updateUserStatistics(userId, "Pong", player1Win, 0);
        } else {
            JOptionPane.showMessageDialog(null, "Error updating statistics. User not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * GamePanel class represents the panel where the game is drawn.
     */
    private class GamePanel extends JPanel {

        public GamePanel() {
            setBackground(Color.BLACK);
            setDoubleBuffered(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Clear the screen before drawing
            drawGame(g);
        }

        private void drawGame(Graphics g) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // Draw paddles
            g.setColor(Color.WHITE);
            g.fillRect(PADDLE_EDGE_OFFSET, player1Paddle.getY(), PADDLE_WIDTH, PADDLE_HEIGHT); // Left paddle
            g.fillRect(panelWidth - PADDLE_EDGE_OFFSET - PADDLE_WIDTH, player2Paddle.getY(), PADDLE_WIDTH, PADDLE_HEIGHT); // Right paddle

            // Draw ball
            g.fillOval(ball.getX(), ball.getY(), BALL_SIZE, BALL_SIZE);

            // Draw scores on either side of the screen
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));

            String player1ScoreText = String.valueOf(player1Score);
            String player2ScoreText = String.valueOf(player2Score);

            FontMetrics scoreMetrics = g.getFontMetrics(g.getFont());

            // Draw Player 1 score on the left side
            int player1ScoreX = (panelWidth / 2) - scoreMetrics.stringWidth(player1ScoreText) - 40; // Position left of the center
            g.drawString(player1ScoreText, player1ScoreX, 50);

            // Draw Player 2 score on the right side
            int player2ScoreX = (panelWidth / 2) + 40; // Position right of the center
            g.drawString(player2ScoreText, player2ScoreX, 50);

            // Draw middle dividing line
            drawMiddleLine(g);
        }

        private void drawMiddleLine(Graphics g) {
            g.setColor(Color.WHITE);
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // Draw a dashed line in the center
            for (int y = 0; y < panelHeight; y += 20) {
                g.fillRect(panelWidth / 2 - 1, y, 2, 10);
            }
        }
    }

    /**
     * Paddle class represents a paddle in the Pong game.
     */
    /**
     * Paddle class represents a paddle in the Pong game.
     */
    private class Paddle {
        private int x, y;

        public Paddle(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void moveUp() {
            if (y - PADDLE_SPEED >= 0) y -= PADDLE_SPEED;
        }

        public void moveDown() {
            if (y + PADDLE_SPEED <= HEIGHT - PADDLE_HEIGHT) y += PADDLE_SPEED;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
        }

        // Add the setPosition method to update paddle's position
        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Ball class represents the ball in the Pong game.
     */
    private class Ball {
        private int x, y, xDir, yDir;

        public Ball(int x, int y) {
            this.x = x;
            this.y = y;
            setDirection(BALL_SPEED, BALL_SPEED);
        }

        public void move() {
            x += xDir;
            y += yDir;
        }

        public void reverseXDirection() {
            xDir = -xDir;
        }

        public void reverseYDirection() {
            yDir = -yDir;
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setDirection(int xDir, int yDir) {
            this.xDir = xDir;
            this.yDir = yDir;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, BALL_SIZE, BALL_SIZE);
        }

        public boolean intersects(Paddle paddle) {
            return getBounds().intersects(paddle.getBounds());
        }
    }
}