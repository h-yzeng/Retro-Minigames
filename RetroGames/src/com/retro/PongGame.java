package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PongGame extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 10;
    private static final int PADDLE_HEIGHT = 100;
    private static final int BALL_SIZE = 20;
    private static final int PADDLE_SPEED = 20;
    private static final int PADDLE_EDGE_OFFSET = 10; // Adjust this value to change the spacing from the edge

    private Timer gameTimer;
    private Paddle player1Paddle, player2Paddle;
    private Ball ball;
    private int player1Score, player2Score;

    public PongGame() {
        setTitle("Pong Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        // Initialize paddles and ball with their starting positions
        player1Paddle = new Paddle(PADDLE_EDGE_OFFSET, HEIGHT / 2 - PADDLE_HEIGHT / 2); // Left Paddle
        player2Paddle = new Paddle(WIDTH - PADDLE_EDGE_OFFSET - PADDLE_WIDTH, HEIGHT / 2 - PADDLE_HEIGHT / 2); // Right Paddle
        ball = new Ball(WIDTH / 2 - BALL_SIZE / 2, HEIGHT / 2 - BALL_SIZE / 2);

        player1Score = 0;
        player2Score = 0;

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
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
        });

        // Game timer to update game state
        gameTimer = new Timer(10, e -> {
            ball.move();
            checkCollision();
            gamePanel.repaint();
        });
        gameTimer.start();

        setFocusable(true);
        setVisible(true);
    }

    private void checkCollision() {
        // Ball collision with top and bottom borders
        if (ball.getY() <= 0 || ball.getY() >= HEIGHT - BALL_SIZE) {
            ball.reverseYDirection();
        }

        // Ball collision with left paddle
        if (ball.getX() <= player1Paddle.getX() + PADDLE_WIDTH &&
            ball.getY() + BALL_SIZE >= player1Paddle.getY() &&
            ball.getY() <= player1Paddle.getY() + PADDLE_HEIGHT) {
            ball.reverseXDirection();
        }

        // Ball collision with right paddle
        if (ball.getX() + BALL_SIZE >= player2Paddle.getX() &&
            ball.getY() + BALL_SIZE >= player2Paddle.getY() &&
            ball.getY() <= player2Paddle.getY() + PADDLE_HEIGHT) {
            ball.reverseXDirection();
        }

        // Check if the ball goes out of bounds (scoring)
        if (ball.getX() <= 0) {
            player2Score++;
            resetBall();
        } else if (ball.getX() >= WIDTH - BALL_SIZE) {
            player1Score++;
            resetBall();
        }
    }

    private void resetBall() {
        ball.setPosition(WIDTH / 2 - BALL_SIZE / 2, HEIGHT / 2 - BALL_SIZE / 2);
        ball.setDirection(-2, 2);
    }

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
            int panelWidth = getWidth(); // Use actual drawable width
            int panelHeight = getHeight(); // Use actual drawable height

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
            int panelWidth = getWidth(); // Ensure the width is from the panel, not the JFrame
            int panelHeight = getHeight(); // Ensure the height is from the panel, not the JFrame

            // Draw a dashed line in the center
            for (int y = 0; y < panelHeight; y += 20) {
                g.fillRect(panelWidth / 2 - 1, y, 2, 10); // Adjust to draw in the correct panel center
            }
        }
    }

    private class Paddle {
        private int x, y;

        public Paddle(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void moveUp() {
            y -= PADDLE_SPEED;
        }

        public void moveDown() {
            y += PADDLE_SPEED;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    private class Ball {
        private int x, y, xDir, yDir;

        public Ball(int x, int y) {
            this.x = x;
            this.y = y;
            this.xDir = -2;
            this.yDir = 2;
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PongGame::new);
    }
}
