package com.retro;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 * DatabaseManager class handles the database connection and operations.
 */
public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/retro_games_db"; // Database URL
    private static final String USER = "root"; // MySQL username
    private static final String PASSWORD = "Hchen43859hY!"; // MySQL password

    /**
     * Establishes a connection to the database.
     *
     * @return Connection object if successful, null otherwise
     */
    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!"); // Debug message
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Updates user statistics after a game.
     *
     * @param userId the ID of the user
     * @param gameName the name of the game played
     * @param isWin true if the game was won, false otherwise
     * @param highScore the high score to update, if applicable
     */
    public static void updateUserStatistics(int userId, String gameName, boolean isWin, int highScore) {
        try (Connection conn = connect()) {
            if (conn != null) {
                String updateStatsQuery = null;
                switch (gameName) {
                    case "TicTacToe":
                        updateStatsQuery = "UPDATE users SET tic_tac_toe_games_played = tic_tac_toe_games_played + 1, tic_tac_toe_games_won = tic_tac_toe_games_won + ? WHERE id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(updateStatsQuery)) {
                            stmt.setInt(1, isWin ? 1 : 0);
                            stmt.setInt(2, userId);
                            stmt.executeUpdate();
                        }
                        break;
                    case "Snake":
                        updateStatsQuery = "UPDATE users SET snake_games_played = snake_games_played + 1, snake_high_score = GREATEST(snake_high_score, ?) WHERE id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(updateStatsQuery)) {
                            stmt.setInt(1, highScore);
                            stmt.setInt(2, userId);
                            stmt.executeUpdate();
                        }
                        break;
                    case "Pong":
                        updateStatsQuery = "UPDATE users SET pong_games_played = pong_games_played + 1, pong_games_won = pong_games_won + ? WHERE id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(updateStatsQuery)) {
                            stmt.setInt(1, isWin ? 1 : 0);
                            stmt.setInt(2, userId);
                            stmt.executeUpdate();
                        }
                        break;
                }

                // Update high scores table
                if (highScore > 0) {
                    String insertHighScoreQuery = "INSERT INTO highscores (user_id, game_name, high_score) VALUES (?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertHighScoreQuery)) {
                        stmt.setInt(1, userId);
                        stmt.setString(2, gameName);
                        stmt.setInt(3, highScore);
                        stmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the leaderboard for a specified game.
     *
     * @param gameName the name of the game for which to retrieve the leaderboard
     * @return ResultSet containing the leaderboard data
     */
    public static ResultSet getLeaderboard(String gameName) {
        try (Connection conn = connect()) {
            String leaderboardQuery = "SELECT u.username, h.high_score FROM highscores h JOIN users u ON h.user_id = u.id WHERE h.game_name = ? ORDER BY h.high_score DESC LIMIT 10";
            PreparedStatement stmt = conn.prepareStatement(leaderboardQuery);
            stmt.setString(1, gameName);
            return stmt.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a user's statistics from the database.
     *
     * @param username the username of the user
     * @return ResultSet containing the user's statistics
     */
    public static ResultSet getUserStatistics(String username) {
        try (Connection conn = connect()) {
            String statsQuery = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(statsQuery);
            stmt.setString(1, username);
            return stmt.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
