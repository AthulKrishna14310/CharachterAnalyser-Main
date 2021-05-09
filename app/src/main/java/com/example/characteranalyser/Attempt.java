package com.example.characteranalyser;

import java.util.ArrayList;

public class Attempt {
    private String Letter;
    private String Ne;
    private String Score;
    private String Speed;
    private String Time;
    private String Total;
    private String user;
    private ArrayList<String> Screens;
    private String AttemptedAt;

    public Attempt() {
    }

    public Attempt(String letter, String ne, String score, String speed,
                   String time, String total, String user,
                   ArrayList<String> screens, String attemptedAt) {
        Letter = letter;
        Ne = ne;
        Score = score;
        Speed = speed;
        Time = time;
        Total = total;
        this.user = user;
        Screens = screens;
        AttemptedAt = attemptedAt;
    }

    public String getLetter() {
        return Letter;
    }

    public void setLetter(String letter) {
        Letter = letter;
    }

    public String getNe() {
        return Ne;
    }

    public void setNe(String ne) {
        Ne = ne;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String speed) {
        Speed = speed;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<String> getScreens() {
        return Screens;
    }

    public void setScreens(ArrayList<String> screens) {
        Screens = screens;
    }

    public String getAttemptedAt() {
        return AttemptedAt;
    }

    public void setAttemptedAt(String attemptedAt) {
        AttemptedAt = attemptedAt;
    }
}