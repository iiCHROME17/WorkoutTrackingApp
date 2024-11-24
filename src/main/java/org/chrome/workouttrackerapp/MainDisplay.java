package org.chrome.workouttrackerapp;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainDisplay extends Application {


    private UserManager uManager;
    private int currentUserId;
    private ProfileImageManager pfpManager;
    private AlertDisplay alertDisplay;
    private DashboardManager dashboardManager;
    private DatabaseConnection databaseConnection;

    @FXML
    private Label activeUserLabel;
    @FXML
    private ImageView activeUserPfp;
    @FXML
    private ScrollPane dashboardPane;

    //Dashboard FXML
    @FXML
    private Label totWorkoutsLabel;
    @FXML
    private Label totExcercisesLabel;
    @FXML
    private Label timeSpentLabel;
    @FXML
    private Label avgDurationLabel;
    @FXML
    private Label workoutsPwLabel;


    /**
     * Constructor for the MainDisplay class
     *
     */

    public MainDisplay() {

        databaseConnection = new DatabaseConnection();
        uManager = new UserManager(databaseConnection);
        pfpManager = new ProfileImageManager();
        alertDisplay = new AlertDisplay();
        dashboardManager = new DashboardManager(databaseConnection);

    }

    /**
     * Initialize method for the MainDisplay class to set the active user label
     */
    @FXML
    private void initialize() {
        currentUserId = uManager.handleLoadUsers();
        refreshDashboard(uManager.getActiveUser());

        activeUserLabel.setText(uManager.getActiveUser().getUsername());
        pfpManager.updateProfileImage(uManager, activeUserPfp);

    }

    /**
     * Method to create a new user and place into the database
     */
    @FXML
    private void createNewUser() {
       if(uManager.createNewUser()) {
           alertDisplay.showAlert(Alert.AlertType.INFORMATION, "User Created", "User has been created successfully.");
           activeUserLabel.setText(uManager.getActiveUser().getUsername());
           pfpManager.updateProfileImage(uManager, activeUserPfp);
       } else {
           alertDisplay.showAlert(Alert.AlertType.ERROR, "Error", "User could not be created.");
       }
    }

    /**
     * Method to load the users menu
     */
    @FXML
    private void loadUsersMenu() {
        currentUserId = uManager.handleLoadUsers();
        activeUserLabel.setText(uManager.getActiveUser().getUsername());
        pfpManager.updateProfileImage(uManager, activeUserPfp);
    }

    /**
     * Method to update the record of the user
     */
    @FXML
    private void updateRecord(){
        uManager.updateUser();
        uManager.loadUser(currentUserId);
        activeUserLabel.setText(uManager.getActiveUser().getUsername());
        pfpManager.updateProfileImage(uManager, activeUserPfp);

    }
    @FXML
    private void dashboardSelected(){
        dashboardPane.visibleProperty().setValue(true);
        dashboardPane.disabledProperty().equals(false);

        User activeUser = uManager.getActiveUser();
        refreshDashboard(activeUser);
    }
    /**
     * Method to refresh the dashboard
     */
    public void refreshDashboard(User activeUser) {
        dashboardManager.calcTotalWorkouts(totWorkoutsLabel);
        dashboardManager.calcTotalExercises(totExcercisesLabel);
        dashboardManager.calcTimeSpent(timeSpentLabel);
        dashboardManager.calcAvgDuration(avgDurationLabel);
        dashboardManager.calcWeeklyWorkouts(workoutsPwLabel);
    }

    /**
     * Method to update Image using the current user's image path
     * Get the image -> copy to the resources\imgs\ProfileImages folder
     * load from the resources\imgs\ProfileImages folder
     */
    public void updateProfileImage() {
        try {
            // Get the active user's image path
            String imagePath = uManager.getActiveUser().getImagePath();

            // Create a File object for the source image
            File sourceFile = new File(imagePath);

            // Validate if the source file exists
            if (!sourceFile.exists()) {
                System.out.println("Source image file does not exist: " + imagePath);
                return;
            }

            // Get the name of the image
            String imageName = sourceFile.getName();

            // Construct the destination path in the resources directory
            String destinationPath = getClass()
                    .getResource("/org/chrome/workouttrackerapp/imgs/ProfileImages/")
                    .getPath()
                    + imageName;

            // Copy the source file to the destination
            File destinationFile = new File(destinationPath);
            try (FileInputStream inputStream = new FileInputStream(sourceFile);
                 FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                System.out.println("Image successfully copied to: " + destinationFile.getAbsolutePath());
            }

            // Load the image using getResource
            Image profileImage = new Image(getClass()
                    .getResource("/org/chrome/workouttrackerapp/imgs/ProfileImages/" + imageName)
                    .toExternalForm());

            // Set the loaded image in the ImageView
            activeUserPfp.setImage(profileImage);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error updating profile image.");
        }
    }

    /**
     * Method to refresh the Profile Data
     */
    public void refreshUser() {
        // 1. Reload the updated user data from the database or data source
        List<User> users = uManager.getUsers(); // This should return the updated list of users from your database
        User activeUser = uManager.getActiveUser(); // You may already have a reference to the active user

        // 2. Find the updated user in the list
        User updatedUser = users.stream()
                .filter(user -> user.getUsername().equals(activeUser.getUsername()))
                .findFirst()
                .orElse(null);

        // 3. If the user exists, update the UI components
        if (updatedUser != null) {
            // Update the username
            activeUserLabel.setText(updatedUser.getUsername());

            // Update the profile image
            String newImagePath = updatedUser.getImagePath();
            if (newImagePath != null && !newImagePath.isEmpty()) {
                Image profileImage = new Image("file:" + newImagePath); // Assuming the image is stored locally
                activeUserPfp.setImage(profileImage);
            }
        } else {
            System.out.println("User not found.");
        }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/chrome/workouttrackerapp/trial/experimental.fxml"));
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
class ProfileImageManager {

    private final List<File> volatileFiles = new ArrayList<>(); // Tracks copied files


    public void updateProfileImage(UserManager uManager, ImageView activeUserPfp) {
        try {
            // Get the active user's image path
            String imagePath = uManager.getActiveUser().getImagePath();
            File sourceFile = new File(imagePath);

            // Validate if the source file exists
            if (!sourceFile.exists()) {
                System.out.println("Source image file does not exist: " + imagePath);
                return;
            }

            String imageName = sourceFile.getName();
            String destinationPath = getClass()
                    .getResource("/org/chrome/workouttrackerapp/imgs/ProfileImages/")
                    .getPath()
                    + imageName;

            File destinationFile = new File(destinationPath);

            // Only copy if the destination file doesn't already exist
            if (!destinationFile.exists()) {
                try (FileInputStream inputStream = new FileInputStream(sourceFile);
                     FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }

                    // Track this file as volatile
                    volatileFiles.add(destinationFile);
                    System.out.println("Image successfully copied to: " + destinationFile.getAbsolutePath());
                }
            }

            // Load the image into the ImageView
            Image profileImage = new Image(getClass()
                    .getResource("/org/chrome/workouttrackerapp/imgs/ProfileImages/" + imageName)
                    .toExternalForm());
            activeUserPfp.setImage(profileImage);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error updating profile image.");
        }
    }

    // Method to clean up volatile files
    public void cleanUpVolatileFiles() {
        for (File file : volatileFiles) {
            if (file.exists() && file.delete()) {
                System.out.println("Deleted volatile file: " + file.getAbsolutePath());
            } else {
                System.out.println("Failed to delete volatile file: " + file.getAbsolutePath());
            }
        }
        volatileFiles.clear();
    }
}

