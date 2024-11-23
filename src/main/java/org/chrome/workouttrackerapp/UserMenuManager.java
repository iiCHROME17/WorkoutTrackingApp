package org.chrome.workouttrackerapp;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserMenuManager {

    private User activeUser;

    public UserMenuManager(){
        // TO BE DONE
    }

    /**
     * Method to create a new User in the database
     *
     */

    public void createNewUser() {
        //Prompt user to enter their name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New User");
        dialog.setHeaderText("Enter your name:");
        dialog.setContentText("Name:");

        //Get input from user
        dialog.showAndWait().ifPresent(name -> {
            //Save name to database
            saveUserToDb(name);

        });
    }

    /**
     * Method to load users from the database
     * Create an instance of User for each user in the database
     * @return
     */
    private List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, strftime('%Y-%m-%d', date_of_creation) AS formatted_date FROM Users";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String formattedDate = rs.getString("formatted_date"); // Retrieve date as a formatted string
                users.add(new User(userId, username, formattedDate)); // Pass formatted date to User
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Method to handle the loading of users
     * Create a new stage to display the users
     */

    public void handleLoadUsers(Label activeUserLabel) {
        // Load users from the database
        List<User> users = loadUsers();

        // New Stage
        Stage userSelectStage = new Stage();
        userSelectStage.initModality(Modality.APPLICATION_MODAL); // Block other windows
        userSelectStage.setTitle("Select User");

        // Create List View to display users
        ListView<String> userListView = new ListView<>();
        for (User user : users) {
            userListView.getItems().add(user.getUsername());
        }

        // Create button to select user
        Button selectButton = new Button("Select User");
        selectButton.setDisable(true); // Disable button until user is selected

        // Enable button when a user is selected
        userListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectButton.setDisable(false);
        });

        // Selection Logic
        selectButton.setOnAction(e -> {
            String selectedUser = userListView.getSelectionModel().getSelectedItem();
            for (User user : users) {
                if (user.getUsername().equals(selectedUser)) {
                    // Set the active user
                    activeUser = user;

                    // Update the label with the active user's name
                    activeUserLabel.setText("Hello, " + activeUser.getUsername() + "!");

                    System.out.println("Active User: " + activeUser);
                    break;
                }
            }
            userSelectStage.close();
        });

        // Layout for pop-up stage
        VBox layout = new VBox(10); // 10px spacing
        layout.getChildren().addAll(new Label("Select User:"), userListView, selectButton); // Add components to layout
        layout.setStyle("-fx-padding: 10;"); // Padding around layout

        // Set scene for window
        Scene scene = new Scene(layout, 300, 400);
        userSelectStage.setScene(scene);
        // Show window
        userSelectStage.showAndWait();
    }




    /**
     * Method to save the user to the database
     *
     * @param name The name of the user
     */

    private void saveUserToDb(String name){
        String sqlCode = "INSERT INTO Users (username,date_of_creation) VALUES (?,?)"; //Place into Users table

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sqlCode)){

            //Set the params for query (username and date registered)
            stmt.setString(1,name);
            stmt.setString(2,java.time.LocalDate.now().toString());

            //Execute the query
            stmt.executeUpdate();

            //Show success message
            showAlert("Success","User created successfully!",AlertType.INFORMATION);
        } catch (SQLException e) {
            //Show error message
            showAlert("Error","Failed to create user",AlertType.ERROR);
        }
    }

    /**
     * Method to show an alert message
     *
     * @param title The title of the alert
     * @param message The message to display
     * @param type The type of alert
     */
    private void showAlert(String title, String message, AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
