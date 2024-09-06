# 🎮 Retro Games Project

This project is a collection of retro-styled games built using **Java**. The project also includes a **login system** backed by a MySQL database to store user credentials, high scores, and game statistics.

## Games Included:
- **Snake** 🐍: A classic arcade game where players control a snake and aim to grow longer without colliding with walls or itself.
- **Pong** 🏓: A two-player table tennis game featuring paddles and a ball. First player to reach 10 points wins!
- **Tic-Tac-Toe** ❌⭕: A two-player strategy game where players alternate placing Xs and Os on a 3x3 grid to win.

## Features:
- **User Authentication**: Users can register, log in, and have their personal data stored securely with hashed passwords.
- **High Scores & Stats**: Each player’s high scores and game statistics (games played, wins) are tracked and displayed in their profile.
- **Leaderboard**: A leaderboard is maintained to show the highest scores across all players.
- **Responsive UI**: Easy-to-use graphical user interface (GUI) built with **Swing**.

## Getting Started

### Prerequisites:
- **Java JDK 8** or higher
- **Eclipse IDE** or any Java-supporting IDE
- **Git** for version control
- **MySQL** or any other relational database for data storage

## Libraries Used
- **jbcrypt**: For secure password hashing.
- **mysql-connector**: To connect the project to MySQL.

### Setting Up the Project

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/h-yzeng/retro-games.git
   cd retro-games

2. **Database Setup**:
- Make sure you have **MySQL** installed and running.
- Import the SQL script located in `/db/create_tables.sql` to set up the required database schema:
  ```bash
  mysql -u your-username -p retro_games_db < db/create_tables.sql

3. **Configure Database**:
- Update the `DatabaseManager.java` file with your MySQL credentials (username, password, database name).

4. **Compile and Run**:
- Open the project in **Eclipse IDE** or your preferred Java IDE.
- Compile the Java files, ensuring the necessary libraries (`jbcrypt` and `mysql-connector`) are in the classpath.
- Run the `LoginScreen` class as the main entry point to the application.

## How to Play

### Snake:
- Use **WASD** keys to control the snake's movement.
- Collect apples to grow longer. Don’t hit the walls or yourself!

### Pong:
- **W** and **S** keys for Player 1 (left paddle).
- **Up** and **Down** arrow keys for Player 2 (right paddle).
- First to 10 points wins.

### Tic-Tac-Toe:
- Click on the empty grid to place your symbol (X or O).
- Get three in a row to win. A line highlights the winning combination.

## How to Run the Project Locally
1. Clone the repository:
   ```bash
   git clone https://github.com/h-yzeng/retro-games.git

2. Setup the database as described above.
3. Open the project in your preferred IDE, compile it, and run the `LoginScreen` class to start.

## Authors
- **Henry Zeng** - [h-yzeng](https://github.com/h-yzeng)

## License
This project is licensed under the MIT License - see the [LICENSE](https://github.com/h-yzeng/Retro-Minigames/blob/master/LICENSE) file for details.

## Future Improvements
- Add more games like Minesweeper or Chess.
- Improve the leaderboard feature with more detailed stats.
- Add sound effects to the games for a better user experience.

![GitHub last commit](https://img.shields.io/github/last-commit/h-yzeng/retro-games)
![GitHub license](https://img.shields.io/github/license/h-yzeng/retro-games)
