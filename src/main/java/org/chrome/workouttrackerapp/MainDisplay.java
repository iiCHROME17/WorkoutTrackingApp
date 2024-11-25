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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainDisplay extends Application {

    private UserManager uManager;
    private DashboardManager dashboardManager;
    private DatabaseConnection dbConnection;
    private int currentUserId;
    private ProfileImageManager pfpManager;
    private AlertDisplay alertDisplay;

    @FXML
    private Label activeUserLabel;
    @FXML
    private ImageView activeUserPfp;
    @FXML
    private ScrollPane dashboardPane;

    // Dashboard FXML elements
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

    // No-argument constructor required by FXML
    public MainDisplay() {
        // Initialize all objects here (e.g., DatabaseConnection, UserManager, etc.)
    }

    @FXML
    private void initialize() throws SQLException {
        // Initialize the dbConnection here, after the no-argument constructor
        dbConnection = new DatabaseConnection(); // Example initialization
        uManager = new UserManager(dbConnection);  // Create UserManager with DB connection
        pfpManager = new ProfileImageManager();
        alertDisplay = new AlertDisplay();
        dashboardManager = new DashboardManager(dbConnection);  // Create DashboardManager with DB connection

        currentUserId = uManager.handleLoadUsers();  // Load users with a single DB connection
        refreshDashboard(uManager.getActiveUser());

        activeUserLabel.setText(uManager.getActiveUser().getUsername());
        pfpManager.updateProfileImage(uManager, activeUserPfp);
    }

    @FXML
    private void createNewUser() throws SQLException {
        if(uManager.createNewUser()) {
            alertDisplay.showAlert(Alert.AlertType.INFORMATION, "User Created", "User has been created successfully.");
            activeUserLabel.setText(uManager.getActiveUser().getUsername());
            pfpManager.updateProfileImage(uManager, activeUserPfp);
        } else {
            alertDisplay.showAlert(Alert.AlertType.ERROR, "Error", "User could not be created.");
        }
    }

    @FXML
    private void loadUsersMenu() throws SQLException {
        currentUserId = uManager.handleLoadUsers();
        activeUserLabel.setText(uManager.getActiveUser().getUsername());
        pfpManager.updateProfileImage(uManager, activeUserPfp);
        refreshDashboard(uManager.getActiveUser());
    }

    @FXML
    private void updateRecord() throws SQLException {
        uManager.updateUser();
        uManager.loadUser(currentUserId);
        activeUserLabel.setText(uManager.getActiveUser().getUsername());
        pfpManager.updateProfileImage(uManager, activeUserPfp);
        refreshDashboard(uManager.getActiveUser());
    }

    @FXML
    private void dashboardSelected() {
        dashboardPane.visibleProperty().set(true);
        dashboardPane.setDisable(false);

        User activeUser = uManager.getActiveUser();
        refreshDashboard(activeUser);
    }

    public void refreshDashboard(User activeUser) {
        dashboardManager.calcTotalWorkouts(totWorkoutsLabel, activeUser.getUserId());
        dashboardManager.calcTotalExercises(totExcercisesLabel,activeUser.getUserId());
        dashboardManager.calcTimeSpent(timeSpentLabel,activeUser.getUserId());
        dashboardManager.calcAvgDuration(avgDurationLabel,activeUser.getUserId());
        dashboardManager.calcWeeklyWorkouts(workoutsPwLabel,activeUser.getUserId());
    }

    public void updateProfileImage() {
        try {
            String imagePath = uManager.getActiveUser().getImagePath();
            File sourceFile = new File(imagePath);

            if (!sourceFile.exists()) {
                System.out.println("Source image file does not exist: " + imagePath);
                return;
            }

            String imageName = sourceFile.getName();
            String destinationPath = getClass().getResource("/org/chrome/workouttrackerapp/imgs/ProfileImages/").getPath() + imageName;
            File destinationFile = new File(destinationPath);

            if (!destinationFile.exists()) {
                try (FileInputStream inputStream = new FileInputStream(sourceFile);
                     FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }
            }

            Image profileImage = new Image(getClass().getResource("/org/chrome/workouttrackerapp/imgs/ProfileImages/" + imageName).toExternalForm());
            activeUserPfp.setImage(profileImage);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error updating profile image.");
        }
    }

    public void refreshUser() throws SQLException {
        List<User> users = uManager.getUsers(); // Using the same connection for this operation
        User activeUser = uManager.getActiveUser();

        User updatedUser = users.stream()
                .filter(user -> user.getUsername().equals(activeUser.getUsername()))
                .findFirst()
                .orElse(null);

        if (updatedUser != null) {
            activeUserLabel.setText(updatedUser.getUsername());

            String newImagePath = updatedUser.getImagePath();
            if (newImagePath != null && !newImagePath.isEmpty()) {
                Image profileImage = new Image("file:" + newImagePath);
                activeUserPfp.setImage(profileImage);
            }
        } else {
            System.out.println("User not found.");
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/chrome/workouttrackerapp/trial/experimental.fxml"));
            AnchorPane root = loader.load();

            primaryStage.setTitle("Workout Tracker");
            Scene scene = new Scene(root, 1280, 720);
            primaryStage.setScene(scene);
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
