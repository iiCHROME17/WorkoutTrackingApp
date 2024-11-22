package org.chrome.workouttrackerapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import  javafx.scene.paint.Color;
import javafx.stage.Stage;

import static javafx.application.Application.launch;


/**
 * A JavaFX application that displays a darked theme canvas.
 * Serves as the main canvas to display the workout tracker's information and graphics.
 */

public class AppCanvas extends Application {

    private Canvas canvas; // global variable to store the canvas

    /**
     * The start method that initialises the JavaFX application.
     * @param primStage the primary stage of the application
     */

    @Override
    public void start(Stage primStage){
        //Create a canvas that is 1280x720 pixels
        canvas = new Canvas(1280, 720);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawCanvas(gc);

        //Create a BorderPane for the layout
        BorderPane root = new BorderPane();

        //Hbox for layout
        HBox layout = new HBox(10);
        layout.setStyle("-fx-background-color: #2D2D2D;");

        //Create menu bar
        MenuBar menuBar = new MenuBar();
        UserManager userManager = new UserManager();

        //pass the canvas and layout to create the user menu
        menuBar.getMenus().add(userManager.createUserMenu(this, layout)); //Add the user menu to the menu bar
        layout.getChildren().add(menuBar); //Add the menu bar to the layout

        root.setTop(menuBar);

        // canvas in the center of the layout (use BorderPane to make the canvas central)
        root.setCenter(canvas); // Add the canvas to the center of the BorderPane

        //Fill the Canvas with a dark gray theme
        gc.setFill(Color.web("#2D2D2D"));
        gc.fillText("Workout Tracker", 1260, 0); //Text, x of the text, y of the text

        //Set the scene with a dark theme
        Scene scene = new Scene(root, 1280, 720, Color.web("#2D2D2D"));
        primStage.setTitle("Workout Tracker");
        primStage.setScene(scene);
        primStage.show();
    }

    /**
     * Method to clear the canvas
     */
    public void clearCanvas() {
        //Clear the canvas
        Canvas canvas = new Canvas(1280, 720);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Method to draw the canvas
     * @param gc the graphics context of the canvas
     */
    private void drawCanvas(GraphicsContext gc){
        //Fill the Canvas with a dark gray theme
        gc.setFill(Color.web("#2D2D2D"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * The main method that launches the JavaFX application.
     * @param args the command line arguments
     */
    public static void main(String[] args){
        launch(args);
    }

}
