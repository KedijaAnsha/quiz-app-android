package com.example.quizapp.data;

/**
 * Model for a quiz result stored in the database.
 */
public class QuizResult {
    private int id;
    private int userId;
    private String category;
    private int score;
    private int totalQuestions;
    private long timeTakenSeconds;
    private String dateTime;

    public QuizResult() {}

    public QuizResult(int userId, String category, int score, int totalQuestions,
                      long timeTakenSeconds, String dateTime) {
        this.userId = userId;
        this.category = category;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timeTakenSeconds = timeTakenSeconds;
        this.dateTime = dateTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public long getTimeTakenSeconds() { return timeTakenSeconds; }
    public void setTimeTakenSeconds(long timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public int getPercentage() {
        return totalQuestions > 0 ? (score * 100) / totalQuestions : 0;
    }

    public String getFormattedTime() {
        long mins = timeTakenSeconds / 60;
        long secs = timeTakenSeconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}
