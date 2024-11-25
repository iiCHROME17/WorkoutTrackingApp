package org.chrome.workouttrackerapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // The absolute path to the database file
    private static final String DATABASE_FILE_PATH = "D:\\Programming\\Java\\Projects\\WorkoutTrackerApp\\src\\main\\resources\\org\\chrome\\workouttrackerapp\\gymProg.db";

    // Static variable to hold the single connection instance
    private static Connection connection;

    // Method to establish a connection to the database
    public static Connection connect() {
        if (connection != null) {
            // Return the existing connection if already established
            return connection;
        }

        try {
            // Log the path of the database being accessed
            System.out.println("Accessing database from path: " + DATABASE_FILE_PATH);

            // Establish connection to the SQLite database using the absolute path
            String databaseUrl = "jdbc:sqlite:" + DATABASE_FILE_PATH;
            connection = DriverManager.getConnection(databaseUrl);

            // Check if the connection was successful
            if (connection != null) {
                System.out.println("Connection to the database established successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while connecting to the database: " + e.getMessage());
        }

        return connection;
    }

    // Method to close the connection
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null; // Set connection to null after closing
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.err.println("Error occurred while closing the connection: " + e.getMessage());
            }
        }
    }
}
