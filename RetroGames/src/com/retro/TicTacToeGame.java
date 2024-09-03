package com.retro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToeGame extends JFrame {
    private char currentPlayer; // To track whose turn it is
    private JButton[] buttons; // Array for Tic-Tac-Toe board buttons
    private char[] board; // Array for Tic-Tac-Toe board state

    public TicTacToeGame() {
        currentPlayer = 'X'; // X always starts first
        buttons = new JButton[9];
        board = new char[9];

        setTitle("Tic Tac Toe");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close this window without exiting the app
        setLayout(new GridLayout(3, 3)); // 3x3 grid for the game board

        initializeBoard();

        setVisible(true); // Show the game window
    }

    private void initializeBoard() {
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 40)); // Font size and style for X/O
            buttons[i].setFocusPainted(false); // Remove focus paint for a cleaner look
            buttons[i].addActionListener(new ButtonClickListener(i)); // Add action listener
            add(buttons[i]); // Add button to the frame
            board[i] = ' '; // Initialize board state
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int index; // Index of the button in the array

        public ButtonClickListener(int index) {
            this.index = index;
        }

        public void actionPerformed(ActionEvent e) {
            if (board[index] == ' ') { // Check if the cell is empty
                board[index] = currentPlayer; // Set the current player's mark
                buttons[index].setText(String.valueOf(currentPlayer)); // Update button text
                if (checkForWin()) { // Check for a win
                    JOptionPane.showMessageDialog(null, currentPlayer + " wins!"); // Display winner
                    resetBoard(); // Reset the board for a new game
                } else if (isBoardFull()) { // Check for a draw
                    JOptionPane.showMessageDialog(null, "It's a draw!");
                    resetBoard(); // Reset the board for a new game
                }
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X'; // Switch player turn
            }
        }
    }

    private boolean checkForWin() {
        // Check rows, columns, and diagonals for a win
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
        currentPlayer = 'X'; // X always starts first in a new game
        for (int i = 0; i < 9; i++) {
            buttons[i].setText("");
            board[i] = ' ';
        }
    }
}
