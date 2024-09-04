package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

    public SnakeGame() {
        setTitle("Snake Game");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setResizable(false);

        initGamePanel();
        startGame();
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
            newApple();
        }
    }

    private void checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // Check if head touches the borders
        if (x[0] < 0 || x[0] >= WINDOW_WIDTH || y[0] < 0 || y[0] >= WINDOW_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    private void draw(Graphics g) {
        if (running) {
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE); // Draw apple

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN); // Snake head
                } else {
                    g.setColor(new Color(45, 180, 0)); // Snake body
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        } else {
            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {
        String message = "Game Over";
        Font font = new Font("Helvetica", Font.BOLD, 20);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.RED);
        g.setFont(font);
        g.drawString(message, (WINDOW_WIDTH - metrics.stringWidth(message)) / 2, WINDOW_HEIGHT / 2);
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
