package org.RestlessTech.TerminaLingo;

public class UserLessonProgress {
    private String lessonPath;
    private int score;

    // No-arg constructor for Gson deserialization
    public UserLessonProgress() {
    }

    public UserLessonProgress(String lessonPath, int score) {
        this.lessonPath = lessonPath;
        this.score = score;
    }

    public String getLessonPath() {
        return lessonPath;
    }

    public void setLessonPath(String lessonPath) {
        this.lessonPath = lessonPath;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Lesson{" + "lessonPath='" + lessonPath + '\'' + ", score=" + score + '}';
    }
}
