package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TicTacToeGame extends JFrame {
    private char currentPlayer;
    private JButton[] buttons;
    private char[] board;
    private int[] winningCombination; // To store the indices of the winning cells
    private boolean gameWon = false; // Track if the game has been won
    private LinePanel linePanel; // Panel to draw the winning line
    private String username;
    private int userId;

    public TicTacToeGame(String username) {
        if (!UserService.isUserLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Access denied! You must be logged in to play.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            this.dispose();
            return;
        }

        this.username = username;
        this.userId = fetchUserIdByUsername(username);

        currentPlayer = 'X';
        buttons = new JButton[9];
        board = new char[9];
        winningCombination = new int[3];

        setTitle("Tic Tac Toe");
        setSize(350, 350); // Slightly larger to fit the new styles
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 3, 10, 10)); // Add spacing between buttons

        linePanel = new LinePanel(); // Initialize the custom panel
        setGlassPane(linePanel); // Add the panel to the glass pane, above the buttons
        linePanel.setVisible(true); // Make the glass pane visible

        initializeBoard();
        setVisible(true);
    }

    private void initializeBoard() {
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.BOLD, 40)); // Larger font for a modern look
            buttons[i].setFocusPainted(false);
            buttons[i].setBackground(new Color(200, 200, 200)); // Light grey background
            buttons[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Add a black border around buttons
            buttons[i].addActionListener(new ButtonClickListener(i));
            add(buttons[i]);
            board[i] = ' ';
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int index;

        public ButtonClickListener(int index) {
            this.index = index;
        }

        public void actionPerformed(ActionEvent e) {
            if (board[index] == ' ' && !gameWon) {
                board[index] = currentPlayer;
                buttons[index].setText(String.valueOf(currentPlayer));
                buttons[index].setForeground(Color.BLACK); // Set text color to black

                SoundManager.playSound("assets/sounds/button_click.wav"); // Play sound when a player makes a move

                if (checkForWin()) {
                    gameWon = true;
                    highlightWinningCells();
                    linePanel.setWinningCells(buttons[winningCombination[0]], buttons[winningCombination[2]]); // Set the start and end buttons for the line
                    handleGameOver(currentPlayer); // Only handle the game over logic here
                } else if (isBoardFull()) {
                    handleGameOver(' '); // No winner, it's a draw
                } else {
                    currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                }
            }
        }
    }

    private boolean checkForWin() {
        return (checkLine(0, 1, 2) || checkLine(3, 4, 5) || checkLine(6, 7, 8) ||
                checkLine(0, 3, 6) || checkLine(1, 4, 7) || checkLine(2, 5, 8) ||
                checkLine(0, 4, 8) || checkLine(2, 4, 6));
    }

    private boolean checkLine(int a, int b, int c) {
        if (board[a] != ' ' && board[a] == board[b] && board[b] == board[c]) {
            winningCombination[0] = a;
            winningCombination[1] = b;
            winningCombination[2] = c;
            return true;
        }
        return false;
    }

    private boolean isBoardFull() {
        for (char c : board) {
            if (c == ' ') {
                return false;
            }
        }
        return true;
    }

    private void highlightWinningCells() {
        for (int i : winningCombination) {
            buttons[i].setBackground(Color.BLACK); // Highlight the winning buttons in black
            buttons[i].setForeground(Color.WHITE); // Change text to white for better contrast
        }
    }

    private void resetBoard() {
        currentPlayer = 'X';
        for (int i = 0; i < 9; i++) {
            buttons[i].setText("");
            buttons[i].setBackground(new Color(200, 200, 200)); // Reset to light grey background
            buttons[i].setForeground(Color.BLACK); // Reset text color to black
            board[i] = ' ';
        }
        gameWon = false;
        linePanel.clearLine(); // Clear the winning line on reset
    }

    // Custom JPanel class to draw the winning line
    private class LinePanel extends JComponent {
        private JButton startButton, endButton; // Buttons that represent the start and end of the line

        public void setWinningCells(JButton start, JButton end) {
            this.startButton = start;
            this.endButton = end;
            repaint(); // Redraw the line when winning cells are set
        }

        public void clearLine() {
            startButton = null;
            endButton = null;
            repaint(); // Clear the line by repainting
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (startButton != null && endButton != null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.WHITE); // Change line color to white
                g2.setStroke(new BasicStroke(5)); // Set the line thickness to 5

                // Get the center points of the start and end buttons
                Point start = SwingUtilities.convertPoint(startButton, startButton.getWidth() / 2, startButton.getHeight() / 2, this);
                Point end = SwingUtilities.convertPoint(endButton, endButton.getWidth() / 2, endButton.getHeight() / 2, this);

                // Draw the line
                g2.drawLine(start.x, start.y, end.x, end.y);
            }
        }
    }

    // Handle game over: show winner and save result to the database
    private void handleGameOver(char winner) {
        if (winner == ' ') {
            SoundManager.playSound("assets/sounds/game_over.wav"); // Play draw sound
            JOptionPane.showMessageDialog(this, "It's a draw!");
        } else {
            SoundManager.playSound("assets/sounds/winning_sound.wav"); // Play winning sound
            JOptionPane.showMessageDialog(this, "Player " + winner + " wins!");
        }

        // Save the game results to the database
        if (UserService.isUserLoggedIn()) {
            saveGameResult("Tic-Tac-Toe", winner);
        }

        askPlayAgain(); // Ask the user if they want to play again
    }

    // Ask the player if they want to play again
    private void askPlayAgain() {
        int response = JOptionPane.showConfirmDialog(this, "Do you want to play again?", "Play Again", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            resetBoard();
        } else {
            this.dispose(); // Close the game window if the player does not want to play again
        }
    }

    // Save the game result to the database
    private void saveGameResult(String gameName, char winner) {
        if (UserService.isUserLoggedIn()) {
            User user = UserService.getLoggedInUser();
            try (Connection conn = DatabaseManager.connect()) {
                String query;
                if (winner != ' ') {
                    query = "UPDATE users SET tic_tac_toe_games_played = tic_tac_toe_games_played + 1, tic_tac_toe_games_won = tic_tac_toe_games_won + 1 WHERE id = ?";
                } else {
                    query = "UPDATE users SET tic_tac_toe_games_played = tic_tac_toe_games_played + 1, tic_tac_toe_games_drawn = tic_tac_toe_games_drawn + 1 WHERE id = ?";
                }

                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, user.getId());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Fetch the user ID based on the username
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
}
