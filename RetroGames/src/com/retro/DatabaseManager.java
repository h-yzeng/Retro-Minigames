package com.retro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/retro_games_db"; // Replace with your DB name
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = "Hchen43859hY!"; // Replace with your MySQL password

    // Method to establish a connection to the database
    public static Connection connect() throws SQLException {
        System.out.println("Attempting to connect to the database..."); // Debug message
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Database connected successfully!"); // Success message
        return conn;
    }
}