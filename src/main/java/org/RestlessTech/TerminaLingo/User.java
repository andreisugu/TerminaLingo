package org.RestlessTech.TerminaLingo;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final List<UserLessonProgress> lessonsCompleted;
    private final List<String> learnedWords;
    private String userName;
    private String password;
    private int dailyLoginStreak;
    private String lastLoginDate;
    private int lessonsTotal;

    public User() {
        this.lessonsCompleted = new ArrayList<>();
        this.learnedWords = new ArrayList<>();
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.dailyLoginStreak = 0;
        this.lessonsTotal = 0;
        this.lastLoginDate = null; // Set on first login
        this.lessonsCompleted = new ArrayList<>();
        this.learnedWords = new ArrayList<>();
    }

    // Getters
    public String getUsername() {
        return userName;
    }

    // Setters
    public void setUsername(String username) {
        this.userName = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDailyLoginStreak() {
        return dailyLoginStreak;
    }

    public void setDailyLoginStreak(int dailyLoginStreak) {
        this.dailyLoginStreak = dailyLoginStreak;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public int getLessonsTotal() {
        return lessonsTotal;
    }

    public void setLessonsTotal(int lessonsTotal) {
        this.lessonsTotal = lessonsTotal;
    }

    public List<UserLessonProgress> getLessonsCompleted() {
        return lessonsCompleted;
    }

    public List<String> getLearnedWords() {
        return learnedWords;
    }

    public void incrementDailyLoginStreak() {
        this.dailyLoginStreak += 1;
    }

    public void incrementLessonsTotal() {
        this.lessonsTotal += 1;
    }

    public void addLessonCompleted(String lessonPath, int score) {
        // Check if the lesson was already completed (lessonPath).
        // If it was, replace only if higher score
        boolean found = false;
        for (UserLessonProgress lesson : lessonsCompleted) {
            if (lesson.getLessonPath().equals(lessonPath)) {
                found = true;
                if (lesson.getScore() < score) {
                    lesson.setScore(score);
                }
                break;
            }
        }
        if (!found) {
            this.lessonsCompleted.add(new UserLessonProgress(lessonPath, score));
        }
    }

    public void addLearnedWord(String word) {
        if (!this.learnedWords.contains(word)) {
            this.learnedWords.add(word);
        }
    }

    @Override
    public String toString() {
        return "User: " + this.userName +
                "\nDaily Login Streak: " + this.dailyLoginStreak +
                "\nTotal Lessons: " + this.lessonsTotal +
                "\nUnique Lessons: " + this.lessonsCompleted.size() +
                "\nAverage Score: " + getAverageLessonScore().toString() +
                "\nLearned words: " + this.learnedWords;
    }

    private Float getAverageLessonScore() {
        int total = 0;
        for (UserLessonProgress lesson : lessonsCompleted) {
            total += lesson.getScore();
        }
        return (float) (total / lessonsCompleted.size());
    }
}
