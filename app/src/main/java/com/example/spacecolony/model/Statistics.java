package com.example.spacecolony.model;

public class Statistics {
    private int missionsPlayed = 0;
    private int missionsWon = 0;
    private int trainingSessions = 0;

    public void incrementMissions() { missionsPlayed++; }
    public void incrementWins() { missionsWon++; }
    public void incrementTraining() { trainingSessions++; }

    public int getMissionsPlayed() { return missionsPlayed; }
    public int getMissionsWon() { return missionsWon; }
    public int getTrainingSessions() { return trainingSessions; }
}