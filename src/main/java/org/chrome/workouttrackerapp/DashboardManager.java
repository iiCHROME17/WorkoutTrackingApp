package org.chrome.workouttrackerapp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardManager {

    private DatabaseConnection databaseConnection;

    public DashboardManager (DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Method to calculate the total number of workouts
     * @param labelToModify the label to set data to
     */
    void calcTotalWorkouts(Label labelToModify){
        try {
            Connection connection = databaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM workouts");
            ResultSet resultSet = preparedStatement.executeQuery();
            int totalWorkouts = resultSet.getInt(1);
            labelToModify.setText(String.valueOf(totalWorkouts));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to calculate the total number of exercises
     * @param labelToModify the label to set data to
     */
    void calcTotalExercises(Label labelToModify){
        try {
            Connection connection = databaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM exercises");
            ResultSet resultSet = preparedStatement.executeQuery();
            int totalExercises = resultSet.getInt(1);
            labelToModify.setText(String.valueOf(totalExercises));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to calculate the average duration of workouts
     * @param labelToModify the label to set data to
     */
    void calcAvgDuration(Label labelToModify) {
        try {
            Connection connection = databaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT AVG(duration) FROM workouts");
            ResultSet resultSet = preparedStatement.executeQuery();
            int avgDuration = resultSet.getInt(1);
            labelToModify.setText(String.valueOf(avgDuration));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to calculate the time spent working out
     * @param labelToModify the label to set data to
     */
    void calcTimeSpent(Label labelToModify) {
        try {
            Connection connection = databaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(duration) FROM workouts");
            ResultSet resultSet = preparedStatement.executeQuery();
            int totalTime = resultSet.getInt(1);
            labelToModify.setText(String.valueOf(totalTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to calculate the workouts done in the last 7 days
     * @param labelToModify the label to set data to
     */
    void calcWeeklyWorkouts(Label labelToModify) {
        try {
            Connection connection = databaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM workouts WHERE date >= DATE('now', '-7 days')");
            ResultSet resultSet = preparedStatement.executeQuery();
            int workoutsLast7Days = resultSet.getInt(1);
            labelToModify.setText(String.valueOf(workoutsLast7Days));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to refresh the display
     */

}
