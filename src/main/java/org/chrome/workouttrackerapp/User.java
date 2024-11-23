package org.chrome.workouttrackerapp;

import java.util.Date;
public class User {
    private int userId;
    private String username;
    private String dateOfCreation;

    public User(int userId, String username, String dateOfCreation) {
        this.userId = userId;
        this.username = username;
        this.dateOfCreation = dateOfCreation;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", dateOfCreation='" + dateOfCreation + '\'' +
                '}';
    }
}
