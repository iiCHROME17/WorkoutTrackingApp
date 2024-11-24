package org.chrome.workouttrackerapp;

import javafx.scene.control.Alert;

public class AlertDisplay {
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
}
