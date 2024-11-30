package org.chrome.workouttrackerapp;

import java.util.ArrayList;
import java.util.List;

public class Workout {
    private String workoutName;
    private List<Exercise> exercises;

    public Workout(String workoutName) {
        this.workoutName = workoutName;
        this.exercises = new ArrayList<>();
    }

    public String getWorkoutName() {
        return workoutName;
    }
    public List<Exercise> getExercises() {
        return exercises;
    }

    public void addExercise(Exercise Exercise) {
        exercises.add(Exercise);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Exercise exercise : exercises) {
            sb.append(exercise.toString()).append("\n");
        }
        return sb.toString();
    }
}
