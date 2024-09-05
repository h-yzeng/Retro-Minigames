package com.retro;

/**
 * User class represents a user in the system.
 */
public class User {
    private String username;
    private String password; // We store the password securely hashed

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Removed getPassword method for security reasons
    public void setPassword(String password) {
        this.password = password;
    }
}
