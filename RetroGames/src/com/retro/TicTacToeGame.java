package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * TicTacToeGame class represents the Tic-Tac-Toe game UI and logic.
 */
public class TicTacToeGame extends JFrame {
    private char currentPlayer;
    private JButton[] buttons;
    private char[] board;
    private String username;
    private int userId;

    public TicTacToeGame(String username) {
        if (!UserService.isUserLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Access denied! You must be logged in to play.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            this.dispose();
            return;
        }

        this.username = username;
        this.userId = fetchUserIdByUsername(username); // Correctly fetch user ID from database

        currentPlayer = 'X';
        buttons = new JButton[9];
        board = new char[9];

        setTitle("Tic Tac Toe");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 3));

        initializeBoard();
        setVisible(true);
    }

    /**
     * Fetches the user ID from the database based on the username.
     *
     * @param username The username to search for.
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

    private void initializeBoard() {
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 40));
            buttons[i].setFocusPainted(false);
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
            if (board[index] == ' ') {
                board[index] = currentPlayer;
                buttons[index].setText(String.valueOf(currentPlayer));
                if (checkForWin()) {
                    JOptionPane.showMessageDialog(null, currentPlayer + " wins!");
                    updateStatistics(true); // Player wins
                    resetBoard();
                } else if (isBoardFull()) {
                    JOptionPane.showMessageDialog(null, "It's a draw!");
                    updateStatistics(false); // Draw
                    resetBoard();
                }
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
            }
        }
    }

    private boolean checkForWin() {
        return (checkLine(0, 1, 2) || checkLine(3, 4, 5) || checkLine(6, 7, 8) ||
                checkLine(0, 3, 6) || checkLine(1, 4, 7) || checkLine(2, 5, 8) ||
                checkLine(0, 4, 8) || checkLine(2, 4, 6));
    }

    private boolean checkLine(int a, int b, int c) {
        return (board[a] != ' ' && board[a] == board[b] && board[b] == board[c]);
    }

    private boolean isBoardFull() {
        for (char c : board) {
            if (c == ' ') {
                return false;
            }
        }
        return true;
    }

    private void resetBoard() {
        currentPlayer = 'X';
        for (int i = 0; i < 9; i++) {
            buttons[i].setText("");
            board[i] = ' ';
        }
    }

    private void updateStatistics(boolean isWin) {
        if (userId != -1) {
            DatabaseManager.updateUserStatistics(userId, "TicTacToe", isWin, 0);
        } else {
            JOptionPane.showMessageDialog(null, "Error updating statistics. User not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
