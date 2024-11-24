package org.chrome.workouttrackerapp;

import java.util.Date;
public class User {
    private int userId;
    private String username;
    private String dateOfCreation;
    private String imagePath;


    public User(int userId, String username, String dateOfCreation, String imagePath) {
        this.userId = userId;
        this.username = username;
        this.dateOfCreation = dateOfCreation;
        this.imagePath = imagePath;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", dateOfCreation='" + dateOfCreation + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
