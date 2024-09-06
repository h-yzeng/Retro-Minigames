CREATE DATABASE IF NOT EXISTS retro_games_db;
USE retro_games_db;

-- Users table to store user credentials and statistics
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    tic_tac_toe_games_played INT DEFAULT 0,
    tic_tac_toe_games_won INT DEFAULT 0,
    snake_games_played INT DEFAULT 0,
    snake_high_score INT DEFAULT 0,
    pong_games_played INT DEFAULT 0,
    pong_games_won INT DEFAULT 0
);

-- Highscores table to store the top scores for each game and user
CREATE TABLE IF NOT EXISTS highscores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    game_name VARCHAR(50),
    high_score INT,
    score_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);