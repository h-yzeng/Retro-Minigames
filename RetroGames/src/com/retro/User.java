package com.retro;

/**
 * User class represents a user in the system with detailed game stats.
 */
public class User {
    private int id;          // User's unique ID from the database
    private String username;
    private String password; // We store the password securely hashed

    // Tic-Tac-Toe stats
    private int ticTacToeGamesPlayed;
    private int ticTacToeGamesWon;
    private int ticTacToeGamesDrawn; // New field for games drawn

    // Snake stats
    private int snakeGamesPlayed;
    private int snakeHighScore;
    private int snakeApplesEaten; // New field for total apples eaten

    // Pong stats
    private int pongGamesPlayed;
    private int pongGamesWon;
    private int pongTotalPoints; // New field for total points scored in Pong

    // Constructor when we already know the user ID (e.g., when retrieving from the database)
    public User(int id, String username, int ticTacToeGamesPlayed, int ticTacToeGamesWon, int ticTacToeGamesDrawn,
                int snakeGamesPlayed, int snakeHighScore, int snakeApplesEaten,
                int pongGamesPlayed, int pongGamesWon, int pongTotalPoints) {
        this.id = id;
        this.username = username;
        this.ticTacToeGamesPlayed = ticTacToeGamesPlayed;
        this.ticTacToeGamesWon = ticTacToeGamesWon;
        this.ticTacToeGamesDrawn = ticTacToeGamesDrawn;
        this.snakeGamesPlayed = snakeGamesPlayed;
        this.snakeHighScore = snakeHighScore;
        this.snakeApplesEaten = snakeApplesEaten;
        this.pongGamesPlayed = pongGamesPlayed;
        this.pongGamesWon = pongGamesWon;
        this.pongTotalPoints = pongTotalPoints;
    }

    // Constructor when we don't know the user ID yet (e.g., during registration)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTicTacToeGamesPlayed() {
        return ticTacToeGamesPlayed;
    }

    public void setTicTacToeGamesPlayed(int ticTacToeGamesPlayed) {
        this.ticTacToeGamesPlayed = ticTacToeGamesPlayed;
    }

    public int getTicTacToeGamesWon() {
        return ticTacToeGamesWon;
    }

    public void setTicTacToeGamesWon(int ticTacToeGamesWon) {
        this.ticTacToeGamesWon = ticTacToeGamesWon;
    }

    public int getTicTacToeGamesDrawn() {
        return ticTacToeGamesDrawn;
    }

    public void setTicTacToeGamesDrawn(int ticTacToeGamesDrawn) {
        this.ticTacToeGamesDrawn = ticTacToeGamesDrawn;
    }

    public int getSnakeGamesPlayed() {
        return snakeGamesPlayed;
    }

    public void setSnakeGamesPlayed(int snakeGamesPlayed) {
        this.snakeGamesPlayed = snakeGamesPlayed;
    }

    public int getSnakeHighScore() {
        return snakeHighScore;
    }

    public void setSnakeHighScore(int snakeHighScore) {
        this.snakeHighScore = snakeHighScore;
    }

    public int getSnakeApplesEaten() {
        return snakeApplesEaten;
    }

    public void setSnakeApplesEaten(int snakeApplesEaten) {
        this.snakeApplesEaten = snakeApplesEaten;
    }

    public int getPongGamesPlayed() {
        return pongGamesPlayed;
    }

    public void setPongGamesPlayed(int pongGamesPlayed) {
        this.pongGamesPlayed = pongGamesPlayed;
    }

    public int getPongGamesWon() {
        return pongGamesWon;
    }

    public void setPongGamesWon(int pongGamesWon) {
        this.pongGamesWon = pongGamesWon;
    }

    public int getPongTotalPoints() {
        return pongTotalPoints;
    }

    public void setPongTotalPoints(int pongTotalPoints) {
        this.pongTotalPoints = pongTotalPoints;
    }
}
