package org.chrome.workouttrackerapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;

public class WorkoutWindowController {

    @FXML
    private TextField workoutNameField;
    @FXML
    private VBox exerciseList;

    private int exerciseCounter = 1; // Counter for exercises
    private int setCounter = 1; // Counter for sets

    //Current workout
    private Workout currentWorkout;

    public enum MuscleGroup {
        UPPER_CHEST, LOWER_CHEST, SHOULDERS, TRAPS, LATS, UPPER_BACK, LOWER_BACK, BICEPS, TRICEPS, FOREARMS, ABS, QUADS, HAMSTRINGS, CALVES
    }

    @FXML
    private void addSet() {
        HBox setRow = new HBox(10);
        setRow.setStyle("-fx-padding: 5;");

        // Set number
        Label setNumberLabel = new Label("Set " + setCounter);
        setNumberLabel.setPrefWidth(50);

        // Weight input
        TextField weightField = new TextField();
        weightField.setPromptText("Weight (kg)");
        weightField.setPrefWidth(100);

        // Reps input
        TextField repsField = new TextField();
        repsField.setPromptText("Reps");
        repsField.setPrefWidth(100);

        // Completion checkbox
        CheckBox completionCheckBox = new CheckBox("Completed");

        // Pass relevant data to the completion handler
        completionCheckBox.setOnAction(event -> handleCompletionCheckBox(completionCheckBox, setRow, exerciseList, workoutNameField.getText(), null));

        // Add all elements to the row
        setRow.getChildren().addAll(setNumberLabel, weightField, repsField, completionCheckBox);

        // Add the row to the exercise list
        exerciseList.getChildren().add(setRow);

        setCounter++; // Increment the set counter
    }

    private void handleCompletionCheckBox(CheckBox completionCheckBox, HBox setRow, VBox setList, String exerciseName, ComboBox<MuscleGroup> muscleGroupComboBox) {
        if (completionCheckBox.isSelected()) {
            System.out.println("Set completed!");

            // You can now access the exercise and set data to save it
            saveSetData(setRow, exerciseName, muscleGroupComboBox, setList);
        } else {
            completionCheckBox.setText(""); // Reset if unchecked
        }
    }

    private void saveSetData(HBox setRow, String exerciseName, ComboBox<MuscleGroup> muscleGroupComboBox, VBox setList) {
        // Here you can collect all the information you need from the setRow, such as:
        String weight = ((TextField) setRow.getChildren().get(1)).getText(); // Weight from the TextField
        String reps = ((TextField) setRow.getChildren().get(2)).getText(); // Reps from the TextField
        String completionStatus = ((CheckBox) setRow.getChildren().get(3)).isSelected() ? "Completed" : "Not Completed";

        // Save the data (e.g., into the Workout or Exercise object)
        Exercise exercise = new Exercise(exerciseName, muscleGroupComboBox.getValue());
        Set set = new Set(setCounter,Integer.parseInt(reps), Double.parseDouble(weight), completionStatus.equals("Completed"));
        exercise.addSet(set);
        currentWorkout.addExercise(exercise);

        // Print the saved data with the required format
        int exerciseIndex = currentWorkout.getExercises().indexOf(exercise) + 1;
        System.out.println("Workout: " + currentWorkout.getWorkoutName());
        System.out.println("Exercise " + exerciseIndex + ": " + exercise.getExerciseName() + ", Muscle Group: " + exercise.getMuscleGroup());

        int setIndex = 1;
        for (Set s : exercise.getSets()) {
            System.out.println("Set " + setIndex + ": Weight: " + s.getWeight() + "kg, Reps: " + s.getReps() + ", Status: " + s.isCompleted());
            setIndex++;
        }
    }

    @FXML
    private void addNewExercise() {
        // Get the exercise name from the workoutNameField
        String exerciseName = workoutNameField.getText().trim();

        // Validate that the exercise name is not empty
        if (exerciseName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Exercise Name Missing");
            alert.setContentText("Please enter a name for the exercise before adding it.");
            alert.showAndWait();
            return;
        }

        // Create a container for the new exercise
        VBox exerciseBox = new VBox(10);
        exerciseBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #f0f0f0;");

        // Set the exercise title
        Label exerciseLabel = new Label(exerciseName); // Use the name entered in the text field
        exerciseLabel.setStyle("-fx-font-weight: bold;");

        // Muscle group dropdown
        ComboBox<MuscleGroup> muscleGroupComboBox = new ComboBox<>();
        muscleGroupComboBox.getItems().setAll(MuscleGroup.values());
        muscleGroupComboBox.setPromptText("Select Muscle Group");

        // Set new workout
        currentWorkout = new Workout(GetTime() + " Workout");
        System.out.println(currentWorkout.getWorkoutName());

        // Set list for this exercise
        VBox setList = new VBox(10);

        // "Add Set" button for exercise
        Button addSetButton = new Button("Add Set");
        addSetButton.setOnAction(event -> {
            HBox setRow = new HBox(10);
            setRow.setStyle("-fx-padding: 5;");

            // Set number
            Label setNumberLabel = new Label("Set " + (setList.getChildren().size() + 1));
            TextField weightField = new TextField();
            weightField.setPromptText("Weight (kg)");

            TextField repsField = new TextField();
            repsField.setPromptText("Reps");

            CheckBox completionCheckBox = new CheckBox("");
            // Pass the required parameters to handleCompletionCheckBox
            completionCheckBox.setOnAction(e -> handleCompletionCheckBox(completionCheckBox, setRow, setList, exerciseName, muscleGroupComboBox));

            setRow.getChildren().addAll(setNumberLabel, weightField, repsField, completionCheckBox);
            setList.getChildren().add(setRow);
        });

        // Add elements to the exercise container
        exerciseBox.getChildren().addAll(exerciseLabel, muscleGroupComboBox, setList, addSetButton);

        // Add the exercise container to the exercise list
        exerciseList.getChildren().add(exerciseBox);

        // Clear the text field for the next exercise name
        workoutNameField.clear();
    }


    private String GetTime() {
        // Get the current time
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        //If it is before 12pm, output Morning, if it is after 6pm, output Evening, otherwise output Afternoon
        if (hour < 12) {
            return "Morning";
        } else if (hour < 18) {
            return "Afternoon";
        } else {
            return "Evening";
        }
    }
}
