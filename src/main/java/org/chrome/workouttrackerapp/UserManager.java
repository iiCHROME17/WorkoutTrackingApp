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

    private DatabaseConnection databaseConnection;
    private User activeUser;

    public User getActiveUser() {
        return activeUser;
    }

    public UserManager() {
        databaseConnection = new DatabaseConnection();
    }

    /**
     * Method to create new user from input dialog
     * Get Date in YYYY-MM-DD format
     * Get Image Path
     * return  true if user is created
     * return false if user is not created
     */

    public boolean createNewUser() {
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
        try (Connection connection = databaseConnection.getConnection();
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

        try (Connection connection = databaseConnection.getConnection();
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

        return users;
    }
    /**
     * Method to handle loading users from the database
     *
     */

    public void handleLoadUsers() {
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
                Button deleteButton = new Button("Delete User");
                deleteButton.setOnAction(e -> deleteUser(selectedUser));

                // You can now add hBox to your layout, e.g., some parent layout in your JavaFX scene
                // For example, in a controller or main method, use: someParentLayout.getChildren().add(hBox);

                System.out.println("Selected User: " + selectedUser);
                setActiveUser(selectedUser);
            }
        });
    }

    /**
     * Method to delete a user from the database
     * @param user the user to delete
     */

    private void deleteUser(User user) {
        String sqlDelete = "DELETE FROM Users WHERE user_id = ?";
        try (Connection connection = databaseConnection.getConnection();
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
    }


}

class DatabaseConnection {

    public Connection getConnection() throws SQLException {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Get the database file from resources
            URL url = getClass().getResource("/org/chrome/workouttrackerapp/gymProg.db");

            if (url == null) {
                throw new IOException("Database file not found in resources.");
            }

            // Convert the URL to a URI, then to a File object
            File databaseFile = new File(url.toURI());

            // Get the connection to the database
            return DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());

        } catch (ClassNotFoundException | SQLException | IOException | URISyntaxException e) {
            throw new SQLException("Failed to connect to the database", e);
        }
    }
}
