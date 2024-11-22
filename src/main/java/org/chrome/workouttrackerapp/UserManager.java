package org.chrome.workouttrackerapp;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserManager {

    private final Connection connection;
    private final Label activeUserLabel; // Label to display the active user

    /**
     * Method to create a new user manager
     */
    public UserManager(){
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/org/chrome/workouttrackerapp/gymProg.db");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
        activeUserLabel = new Label("Active User: None"); // Create a new label to display the active user
    }

    /**
     * Menu Containing the User Options, New User and Load User
     * @param canvas the canvas to display the user menu
     * @param layout the layout to display the user menu
     * @return the user menu
     */
    public Menu createUserMenu(AppCanvas canvas, HBox layout){
        Menu userMenu = new Menu("User"); // Create a new menu representing the user

        MenuItem newUser = new MenuItem("New User"); // Create a new menu item to add a new user
        newUser.setOnAction(event -> createNewUser(layout)); // When the menu item is clicked, call the createNewUser method

        MenuItem loadUser = new MenuItem("Load User"); // Create a new menu item to load an existing user
        loadUser.setOnAction(event -> loadUser(canvas, layout)); // When the menu item is clicked, call the loadUser method

        userMenu.getItems().addAll(newUser, loadUser); // Add the menu items to the user menu
        return userMenu;
    }

    /**
     * Method used to create a new user taking inputs then executing SQL to insert the new user into the database
     */
    private void createNewUser(HBox layout){
        TextInputDialog dialog = new TextInputDialog(); // Create a new dialog box to get the user's name
        dialog.setTitle("New User Registration"); // Set the title of the dialog box
        dialog.setHeaderText("Create a New User"); // Set the header text of the dialog box
        dialog.setContentText("Enter a username"); // Set the content text of the dialog box

        dialog.showAndWait().ifPresent(username -> {
            String sqlCode = "INSERT INTO Users (username, date_of_creation) VALUES (?, ?)";
            try (PreparedStatement prepStatement = connection.prepareStatement(sqlCode)) { // Prepare the SQL statement
                prepStatement.setString(1, username); // Set the username
                prepStatement.setString(2, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))); // Set the date of creation
                prepStatement.executeUpdate(); // Run the SQL statement

                activeUserLabel.setText("Active User: " + username); // Set the active user label to the new user

                // Ensure the active user label is added only once
                if (!layout.getChildren().contains(activeUserLabel)) {
                    layout.getChildren().add(activeUserLabel); // Add the active user label to the layout
                }

                showAlert(Alert.AlertType.INFORMATION, "User Created", "User " + username + " has been created"); // Show an alert to confirm the user has been created
            } catch (SQLException e) {
                throw new RuntimeException("Failed to create a new user", e); // If fails, throw an error to log
            }
        });
    }

    /**
     * Method to load existing users from the database
     * @param canvas the canvas to display the user data
     * @param layout the layout to display the user menu
     */
    private void loadUser(AppCanvas canvas, HBox layout){
        String sqlCode = "SELECT username FROM Users";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlCode);
             ResultSet rs = pstmt.executeQuery()) {

            // Create a pop-up to display user list
            Stage popup = new Stage();
            popup.setTitle("Load User");

            VBox userList = new VBox(10);
            userList.setStyle("-fx-padding: 10; -fx-background-color: #1e1e1e; -fx-border-color: #333;");

            while (rs.next()) {
                String username = rs.getString("username");
                Button userButton = new Button(username);
                userButton.setStyle("-fx-background-color: #333; -fx-text-fill: white;");
                userButton.setOnAction(event -> {
                    loadUserData(username, canvas, layout);
                    popup.close();
                });
                userList.getChildren().add(userButton);
            }

            Scene scene = new Scene(userList, 300, 400);
            popup.setScene(scene);
            popup.show();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users: " + e.getMessage());
        }
    }

    /**
     * Method to load user data from the database
     * @param username the username of the user to load
     * @param canvas the canvas to display the user data
     * @param layout the layout to display the active user label
     */
    private void loadUserData(String username, AppCanvas canvas, HBox layout){
        String sqlCode = "SELECT * FROM Users WHERE username = ?"; // Select all users where the username is equal to the input
        try (PreparedStatement pstmt = connection.prepareStatement(sqlCode)){
            pstmt.setString(1, username); // Set the username
            try (ResultSet rs = pstmt.executeQuery()) { // Execute the SQL statement
                if (rs.next()) {
                    String msg = "User Loaded: " + username + "\nCreated on: " + rs.getString("date_of_creation");
                    showAlert(Alert.AlertType.INFORMATION, "User Loaded", msg);

                    // Update the active user label
                    activeUserLabel.setText("Active User: " + username);

                    // Ensure the active user label is added only once
                    if (!layout.getChildren().contains(activeUserLabel)) {
                        layout.getChildren().add(activeUserLabel);
                    }

                    canvas.clearCanvas(); // Clear the canvas
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user data: " + e.getMessage());
        }
    }

    /**
     * Method to show an alert dialog
     * @param type the type of alert to show
     * @param alertTitle the title of the alert
     * @param message the message to display in the alert
     */
    private void showAlert(Alert.AlertType type, String alertTitle, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(alertTitle);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
