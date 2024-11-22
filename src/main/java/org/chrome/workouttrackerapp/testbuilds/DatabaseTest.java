package org.chrome.workouttrackerapp.testbuilds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) {
        try {
            // Load the SQLite JDBC driver (not needed for newer versions of Java)
            Class.forName("org.sqlite.JDBC");

            // Connect to SQLite database
            Connection connection = DriverManager.getConnection("jdbc:sqlite:Database.db");



            System.out.println("Connection to SQLite database successful!");
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("The Connection has failed: " + e.getMessage());
        }
    }
}
