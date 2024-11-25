package org.chrome.workouttrackerapp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardManager {

    private DatabaseConnection databaseConnection;

    public DashboardManager(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Method to calculate the total number of workouts for a specific user
     * @param labelToModify the label to set data to
     * @param userId the ID of the user
     */
    void calcTotalWorkouts(Label labelToModify, int userId) {
        String query = "SELECT COUNT(*) FROM workouts WHERE user_id = ?";
        executeQuery(query, resultSet -> {
            try {
                int totalWorkouts = resultSet.getInt(1);
                Platform.runLater(() -> labelToModify.setText(String.valueOf(totalWorkouts)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, userId);
    }

    /**
     * Method to calculate the total number of exercises for a specific user
     * @param labelToModify the label to set data to
     * @param userId the ID of the user
     */
    void calcTotalExercises(Label labelToModify, int userId) {
        String query = "SELECT COUNT(*) FROM exercises WHERE workout_id IN (SELECT workout_id FROM workouts WHERE user_id = ?)";
        executeQuery(query, resultSet -> {
            try {
                int totalExercises = resultSet.getInt(1);
                Platform.runLater(() -> labelToModify.setText(String.valueOf(totalExercises)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, userId);
    }

    /**
     * Method to calculate the average duration of workouts for a specific user
     * @param labelToModify the label to set data to
     * @param userId the ID of the user
     */
    void calcAvgDuration(Label labelToModify, int userId) {
        String query = "SELECT AVG(duration) FROM workouts WHERE user_id = ?";
        executeQuery(query, resultSet -> {
            try {
                int avgDuration = resultSet.getInt(1);
                Platform.runLater(() -> labelToModify.setText(String.valueOf(avgDuration)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, userId);
    }

    /**
     * Method to calculate the total time spent working out for a specific user
     * @param labelToModify the label to set data to
     * @param userId the ID of the user
     */
    void calcTimeSpent(Label labelToModify, int userId) {
        String query = "SELECT SUM(duration) FROM workouts WHERE user_id = ?";
        executeQuery(query, resultSet -> {
            try {
                int totalTime = resultSet.getInt(1);
                Platform.runLater(() -> labelToModify.setText(String.valueOf(totalTime)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, userId);
    }

    /**
     * Method to calculate the workouts done by a specific user in the last 7 days
     * @param labelToModify the label to set data to
     * @param userId the ID of the user
     */
    void calcWeeklyWorkouts(Label labelToModify, int userId) {
        String query = "SELECT COUNT(*) FROM workouts WHERE user_id = ? AND date >= DATE('now', '-7 days')";
        executeQuery(query, resultSet -> {
            try {
                int workoutsLast7Days = resultSet.getInt(1);
                Platform.runLater(() -> labelToModify.setText(String.valueOf(workoutsLast7Days)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, userId);
    }

    /**
     * Utility method to execute a query and handle the result
     * @param query the SQL query to execute
     * @param resultHandler the handler to process the result set
     * @param userId the ID of the user to filter the data
     */
    private void executeQuery(String query, ResultSetHandler resultHandler, int userId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = databaseConnection.connect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);  // Set the user_id parameter
            resultSet = preparedStatement.executeQuery();
            resultHandler.handle(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Ensure resources are closed to prevent leaks
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        databaseConnection.closeConnection();
    }

    /**
     * Functional interface to handle the result set
     */
    @FunctionalInterface
    private interface ResultSetHandler {
        void handle(ResultSet resultSet) throws SQLException;
    }
}
