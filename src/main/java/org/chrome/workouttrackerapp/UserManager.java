package org.chrome.workouttrackerapp;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserManager {

    private final DatabaseConnection databaseConnection;
    private AlertDisplay alertDisplay;
    private User activeUser;

    public User getActiveUser() {
        return activeUser;
    }

    public UserManager(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        alertDisplay = new AlertDisplay();
    }

    /**
     * Method to create new user from input dialog
     * Get Date in YYYY-MM-DD format
     * Get Image Path
     * return  true if user is created
     * return false if user is not created
     */

    public boolean createNewUser() throws SQLException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New User");
        dialog.setHeaderText("Enter a username to create a new user");
        dialog.setContentText("Username:");

        // Get the current date in YYYY-MM-DD format
        LocalDate date = LocalDate.now();
        String currentDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // Display the dialog and handle the input
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String username = result.get().trim();

            // Let the user choose an image
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose an Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));

            Stage stage = new Stage();
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                String imagePath = selectedFile.getAbsolutePath();

                // Create a new user
                User newUser = new User(1, username, currentDate, imagePath);
                System.out.println("User created: " + newUser);

                // Add the user to the database
                addUserToDatabase(newUser);
                //addUserToDatabase(newUser);

                // Load this user as the active user
                setActiveUser(newUser);

                // Return true since the user was successfully created
                return true;
            } else {
                System.out.println("No image selected. User creation aborted.");
            }
        } else {
            System.out.println("No username provided. User creation aborted.");
        }

        // Return false if the user creation failed
        return false;
    }

    public void addUserToDatabase(User user) {
        String sqlCode = "INSERT INTO Users(username, date_of_creation,image_path) VALUES(?, ?, ?)";
        try (Connection connection = databaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlCode)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getDateOfCreation());
            preparedStatement.setString(3, user.getImagePath());

            preparedStatement.executeUpdate();
            System.out.println("User added to the database successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setActiveUser(User user) {
        activeUser = user;
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sqlQuery = "SELECT user_id, username, date_of_creation,image_path FROM Users";

        try (Connection connection = databaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Loop through the result set and create User objects
            while (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String dateOfCreation = resultSet.getString("date_of_creation");
                String imagePath = resultSet.getString("image_path");

                // Create a User object and add it to the list
                User user = new User(id, username, dateOfCreation,imagePath);
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        databaseConnection.closeConnection();
        return users;
    }
    /**
     * Method to handle loading users from the database
     *
     */

    public int handleLoadUsers() throws SQLException {
        List<User> users = getUsers();

        // Create a list of usernames for the choice dialog
        List<String> usernames = users.stream()
                .map(User::getUsername)
                .toList();

        // Create a ChoiceDialog for selecting a user
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, usernames);
        dialog.setTitle("Select User");
        dialog.setHeaderText("Choose a user to load:");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(username -> {
            // Find the selected user based on the username
            User selectedUser = users.stream()
                    .filter(user -> user.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);

            if (selectedUser != null) {

                // Set up a button to delete the selected user
                //Button deleteButton = new Button("Delete User");
               // deleteButton.setOnAction(e -> deleteUser(selectedUser));

                System.out.println("Selected User: " + selectedUser);
                setActiveUser(selectedUser);

            }

        });
        return getActiveUser().getUserId();
    }

    /**
     * Method to delete a user from the database
     * @param user the user to delete
     */

    private void deleteUser(User user) {
        String sqlDelete = "DELETE FROM Users WHERE user_id = ?";
        try (Connection connection = databaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlDelete)) {

            preparedStatement.setInt(1, user.getUserId());
            preparedStatement.executeUpdate();
            System.out.println("User deleted: " + user);

            // Optionally show a confirmation alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("User Deleted");
            alert.setHeaderText("User has been successfully deleted.");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to delete user.");
            alert.showAndWait();
        }

        databaseConnection.closeConnection();
    }
    /**
     * Method to take in int id and load user from database
     * @param id the id of the user to load
     *
     */
    public User loadUser(int id) throws SQLException {
        //For each user in the list of users, check if the user id matches the id passed in
        List<User> users = getUsers();
        User selectedUser = users.stream()
                .filter(user -> user.getUserId() == id)
                .findFirst()
                .orElse(null);
        //if one has been found, set the active user to the selected user
        if (selectedUser != null) {
            setActiveUser(selectedUser);
            System.out.println("User loaded: " + selectedUser);
            return selectedUser;
        } else {
            System.out.println("User not found.");
        }
        return null;
    }

    /**
     * Method to update the record of a user in a database,
     * only the username and image path can be updated
     * Create a window that takes in a new username and a button to open file chooser
     * Update the user record in the database
     *
     */

    public void updateUser () throws SQLException {
        // 1. Load all users
        List<User> users = getUsers(); // This should return a list of User objects from your database or list

        // 2. Create a list of usernames for the choice dialog
        List<String> usernames = users.stream()
                .map(User::getUsername)
                .toList();

        // 3. Create a ChoiceDialog for selecting a user
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, usernames);
        dialog.setTitle("Select User");
        dialog.setHeaderText("Choose a user to load:");
        dialog.setContentText("Username:");

        // 4. Show the dialog and handle user selection
        Optional<String> result = dialog.showAndWait();

        // If a user was selected, proceed with updating
        result.ifPresent(selectedUsername -> {
            // Find the user object corresponding to the selected username
            User selectedUser = users.stream()
                    .filter(user -> user.getUsername().equals(selectedUsername))
                    .findFirst()
                    .orElse(null);

            if (selectedUser != null) {
                // 5. Create a dialog for updating the username
                TextInputDialog updateDialog = new TextInputDialog(selectedUser.getUsername());
                updateDialog.setTitle("Update User");
                updateDialog.setHeaderText("Enter a new username to update the user");
                updateDialog.setContentText("New Username:");

                // Show the dialog and get the new username
                Optional<String> newUsernameResult = updateDialog.showAndWait();

                newUsernameResult.ifPresent(newUsername -> {
                    // 6. Create a FileChooser for selecting an image
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));

                    // Show the file chooser to select a new image
                    File selectedImageFile = fileChooser.showOpenDialog(new Stage());

                    if (selectedImageFile != null) {
                        // Get the image file path
                        String newImagePath = selectedImageFile.getAbsolutePath();

                        // 7. Update the user object with the new username and image path
                        selectedUser.setUsername(newUsername);
                        selectedUser.setImagePath(newImagePath);

                        // 8. Save the updated user to the database (simulated here)
                        updateUserInDatabase(selectedUser);

                        // Show confirmation dialog
                        alertDisplay.showAlert(Alert.AlertType.CONFIRMATION, "User Updated", "User has been updated successfully.");

                    }
                    else {
                        // Continue with original image, new username
                        selectedUser.setUsername(newUsername);
                        updateUserInDatabase(selectedUser);
                        alertDisplay.showAlert(Alert.AlertType.CONFIRMATION, "User Updated", "User has been updated successfully, Old image remains.");

                    }
                });
            }
        });
    }

    /**
     * Method to update the user in the database
     *
     * @param user
     */
    private void updateUserInDatabase(User user) {
        String sqlUpdate = "UPDATE Users SET username = ?, image_path = ? WHERE user_id = ?";
        try (Connection connection = databaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getImagePath());
            preparedStatement.setInt(3, user.getUserId());

            preparedStatement.executeUpdate();
            System.out.println("User updated: " + user);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        databaseConnection.closeConnection();
    }




}


