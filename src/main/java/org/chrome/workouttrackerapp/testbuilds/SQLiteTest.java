package org.chrome.workouttrackerapp.testbuilds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteTest {

    public static void main(String[] args) {
        testQueryData();
    }

    public static void testConnection() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/org/chrome/workouttrackerapp/Database.db")) {
            if (connection != null) {
                System.out.println("Connection successful!");
            } else {
                System.out.println("Connection failed.");
            }
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    public static void testInsertData() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/org/chrome/workouttrackerapp/Database.db");
             Statement stmt = connection.createStatement()) {
            String insertSQL = "INSERT INTO Workouts (exercise_name, duration) VALUES ('Running', 30);";
            int rowsAffected = stmt.executeUpdate(insertSQL);
            if (rowsAffected == 1) {
                System.out.println("Insert successful.");
            } else {
                System.out.println("Insert failed.");
            }
        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
        }
    }

    public static void testQueryData() {
        String dbUrl = "jdbc:sqlite:src/main/resources/org/chrome/workouttrackerapp/gymProg.db";

        // Query to calculate total weight for the first workout
        String query = """
        SELECT SUM(s.weight) AS total_weight
        FROM Workouts w
        JOIN Exercises e ON w.workout_id = e.workout_id
        JOIN Sets s ON e.exercise_id = s.exercise_id
        WHERE w.workout_id = 1; -- Assuming the first workout has ID 1
        """;

        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                double totalWeight = rs.getDouble("total_weight");
                System.out.println("Total weight lifted in the first workout: " + totalWeight + " kg");
            } else {
                System.out.println("No data found for the first workout.");
            }

        } catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
        }
    }

}
