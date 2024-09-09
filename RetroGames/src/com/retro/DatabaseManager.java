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
     * Updates user statistics after a Tic-Tac-Toe game.
     * This method tracks wins and draws for Tic-Tac-Toe only.
     */
    public static void updateUserStatistics(int userId, String gameName, boolean isWin, boolean isDraw) {
        try (Connection conn = connect()) {
            if (conn != null && "tic-tac-toe".equalsIgnoreCase(gameName)) {
                String updateStatsQuery = "UPDATE users SET tic_tac_toe_games_played = tic_tac_toe_games_played + 1, " +
                                          "tic_tac_toe_games_won = tic_tac_toe_games_won + ?, tic_tac_toe_games_drawn = tic_tac_toe_games_drawn + ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateStatsQuery)) {
                    stmt.setInt(1, isWin ? 1 : 0); // Increment wins if it's a win
                    stmt.setInt(2, isDraw ? 1 : 0); // Increment draws if it's a draw
                    stmt.setInt(3, userId);
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Updates user statistics for games like Snake and Pong that don't track draws.
     * This method is used for games like Snake and Pong.
     */
    public static void updateUserStatistics(int userId, String gameName, boolean isWin, int additionalData) {
        try (Connection conn = connect()) {
            if (conn != null) {
                String updateStatsQuery = null;
                
                switch (gameName.toLowerCase()) {
                    case "snake":
                        updateStatsQuery = "UPDATE users SET snake_games_played = snake_games_played + 1, " +
                                           "snake_high_score = GREATEST(snake_high_score, ?), snake_apples_eaten = snake_apples_eaten + ? WHERE id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(updateStatsQuery)) {
                            stmt.setInt(1, additionalData); // High score
                            stmt.setInt(2, additionalData); // Apples eaten
                            stmt.setInt(3, userId);
                            stmt.executeUpdate();
                        }
                        break;

                    case "pong":
                        updateStatsQuery = "UPDATE users SET pong_games_played = pong_games_played + 1, " +
                                           "pong_games_won = pong_games_won + ?, pong_total_points = pong_total_points + ? WHERE id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(updateStatsQuery)) {
                            stmt.setInt(1, isWin ? 1 : 0); // Increment wins if it's a win
                            stmt.setInt(2, additionalData); // Add total points scored during the game
                            stmt.setInt(3, userId);
                            stmt.executeUpdate();
                        }
                        break;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Updates or inserts a high score for a user.
     */
    public static void updateHighScore(int userId, String gameName, int highScore) {
        try (Connection conn = connect()) {
            if (conn != null) {
                String insertHighScoreQuery = "INSERT INTO highscores (user_id, game_name, high_score) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE high_score = GREATEST(high_score, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertHighScoreQuery)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, gameName);
                    stmt.setInt(3, highScore);
                    stmt.setInt(4, highScore); // For the ON DUPLICATE KEY UPDATE
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the leaderboard for a specified game.
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