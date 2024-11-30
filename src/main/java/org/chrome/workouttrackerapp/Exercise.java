package org.chrome.workouttrackerapp;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
    public String exerciseName;
    private WorkoutWindowController.MuscleGroup muscleGroup;
    private List<Set> sets;

    public Exercise(String exerciseName, WorkoutWindowController.MuscleGroup muscleGroup) {
        this.exerciseName = exerciseName;
        this.muscleGroup = muscleGroup;
        this.sets = new ArrayList<>();
    }

    public String getExerciseName() {
        return exerciseName;
    }
    public WorkoutWindowController.MuscleGroup getMuscleGroup() {
        return muscleGroup;
    }
    public List<Set> getSets() {
        return sets;
    }
    public void addSet(Set set) {
        sets.add(set);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Set set : sets) {
            sb.append(set.toString()).append("\n");
        }
        return sb.toString();
    }
}
