package org.chrome.workouttrackerapp;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainDisplay extends Application {


    private UserMenuManager uManager;
    @FXML
    private Label activeUserLabel;

    /**
     * Constructor for the MainDisplay class
     *
     */

    public MainDisplay() {
        uManager = new UserMenuManager();
    }

    /**
     * Initialize method for the MainDisplay class to set the active user label
     */
    @FXML
    private void initialize() {
        uManager.handleLoadUsers(activeUserLabel);
    }

    /**
     * Method to create a new user and place into the database
     */
    @FXML
    private void createNewUser() {
        uManager.createNewUser();
    }
    @FXML
    private void loadUsersMenu() {
        uManager.handleLoadUsers(activeUserLabel);
    }


    /**
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file into an AnchorPane
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/chrome/workouttrackerapp/appLayout.fxml"));
            AnchorPane root = loader.load();

            // Set the title of the window
            primaryStage.setTitle("Workout Tracker");

            // Set the scene with the loaded FXML content
            Scene scene = new Scene(root, 1280, 720);

            // Set the scene to the primaryStage
            primaryStage.setScene(scene);

            // Show the window
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        launch(args);
    }
}
