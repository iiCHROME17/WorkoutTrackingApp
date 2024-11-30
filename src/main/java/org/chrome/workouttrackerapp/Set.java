package org.chrome.workouttrackerapp;

public class Set {
    public int setNumber;
    public int reps;
    public double weight;
    private boolean completed;

    public Set(int setNumber, int reps, double weight, boolean completed) {
        this.setNumber = setNumber;
        this.reps = reps;
        this.weight = weight;
        this.completed = completed;
    }

    public int getSetNumber() {
        return setNumber;
    }
    public int getReps() {
        return reps;
    }
    public double getWeight() {
        return weight;
    }
    public boolean isCompleted() {
        return completed;
    }


    public String toString() {
        return "Set " + setNumber + ": " + reps + " reps at " + weight + " lbs";
    }
}
