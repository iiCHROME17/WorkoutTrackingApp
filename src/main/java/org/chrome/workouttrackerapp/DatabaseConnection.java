package org.chrome.workouttrackerapp;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public Connection getConnection() throws SQLException {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Get the database file from resources
            URL url = getClass().getResource("/org/chrome/workouttrackerapp/gymProg.db");

            if (url == null) {
                throw new IOException("Database file not found in resources.");
            }

            // Convert the URL to a URI, then to a File object
            File databaseFile = new File(url.toURI());

            // Get the connection to the database
            return DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());

        } catch (ClassNotFoundException | SQLException | IOException | URISyntaxException e) {
            throw new SQLException("Failed to connect to the database", e);
        }
    }
}