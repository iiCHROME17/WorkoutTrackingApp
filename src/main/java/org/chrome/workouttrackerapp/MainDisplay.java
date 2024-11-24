package org.chrome.workouttrackerapp;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
    private ProfileImageManager pfpManager;
    @FXML
    private Label activeUserLabel;
    @FXML
    private ImageView activeUserPfp;

    /**
     * Constructor for the MainDisplay class
     *
     */

    public MainDisplay() {

        uManager = new UserManager();
        pfpManager = new ProfileImageManager();

    }

    /**
     * Initialize method for the MainDisplay class to set the active user label
     */
    @FXML
    private void initialize() {
        uManager.handleLoadUsers();

        activeUserLabel.setText(uManager.getActiveUser().getUsername());
        pfpManager.updateProfileImage(uManager, activeUserPfp);

    }

    /**
     * Method to create a new user and place into the database
     */
    @FXML
    private void createNewUser() {
       if(uManager.createNewUser()) {
           showAlert(Alert.AlertType.INFORMATION, "User Created", "User has been created successfully.");
           activeUserLabel.setText(uManager.getActiveUser().getUsername());
           pfpManager.updateProfileImage(uManager, activeUserPfp);
       } else {
           showAlert(Alert.AlertType.ERROR, "Error", "User could not be created.");
       }
    }
    @FXML
    private void loadUsersMenu() {
        uManager.handleLoadUsers();
        activeUserLabel.setText(uManager.getActiveUser().getUsername());
        pfpManager.updateProfileImage(uManager, activeUserPfp);
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

    /**
     * Method to Show Alert Dialog
     * @param alertType the type of alert
     *@param title the title of the alert
     * @param message the message to display
     */
    public void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
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

