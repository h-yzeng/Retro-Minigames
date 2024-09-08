package com.retro;

/**
 * User class represents a user in the system.
 */
public class User {
    private int id;          // User's unique ID from the database
    private String username;
    private String password; // We store the password securely hashed

    // Constructor when we already know the user ID (e.g., when retrieving from the database)
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
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

    public void setPassword(String password) {
        this.password = password;
    }

    // Optionally, you can remove setId to ensure ID stays immutable once set
    public void setId(int id) {
        this.id = id;
    }
}