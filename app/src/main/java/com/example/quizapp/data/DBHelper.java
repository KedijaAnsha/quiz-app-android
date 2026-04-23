package com.example.quizapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quizzy.db";
    private static final int DATABASE_VERSION = 3;

    // Questions table
    private static final String TABLE_QUESTIONS = "questions";
    private static final String COL_Q_ID = "id";
    private static final String COL_Q_QUESTION = "question";
    private static final String COL_Q_OPTION1 = "option1";
    private static final String COL_Q_OPTION2 = "option2";
    private static final String COL_Q_OPTION3 = "option3";
    private static final String COL_Q_OPTION4 = "option4";
    private static final String COL_Q_CORRECT = "correctAnswer";
    private static final String COL_Q_CORRECT_TEXT = "correctAnswerText";
    private static final String COL_Q_CATEGORY = "category";
    private static final String COL_Q_TYPE = "questionType";

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_U_ID = "id";
    private static final String COL_U_USERNAME = "username";
    private static final String COL_U_EMAIL = "email";
    private static final String COL_U_PASSWORD = "password";

    // Results table
    private static final String TABLE_RESULTS = "results";
    private static final String COL_R_ID = "id";
    private static final String COL_R_USER_ID = "userId";
    private static final String COL_R_CATEGORY = "category";
    private static final String COL_R_SCORE = "score";
    private static final String COL_R_TOTAL = "totalQuestions";
    private static final String COL_R_TIME = "timeTaken";
    private static final String COL_R_DATE = "dateTime";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create questions table
        db.execSQL("CREATE TABLE " + TABLE_QUESTIONS + " (" +
                COL_Q_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_Q_QUESTION + " TEXT, " +
                COL_Q_OPTION1 + " TEXT, " +
                COL_Q_OPTION2 + " TEXT, " +
                COL_Q_OPTION3 + " TEXT, " +
                COL_Q_OPTION4 + " TEXT, " +
                COL_Q_CORRECT + " INTEGER, " +
                COL_Q_CORRECT_TEXT + " TEXT, " +
                COL_Q_CATEGORY + " TEXT, " +
                COL_Q_TYPE + " TEXT)");

        // Create users table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_U_USERNAME + " TEXT UNIQUE, " +
                COL_U_EMAIL + " TEXT UNIQUE, " +
                COL_U_PASSWORD + " TEXT)");

        // Create results table
        db.execSQL("CREATE TABLE " + TABLE_RESULTS + " (" +
                COL_R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_R_USER_ID + " INTEGER, " +
                COL_R_CATEGORY + " TEXT, " +
                COL_R_SCORE + " INTEGER, " +
                COL_R_TOTAL + " INTEGER, " +
                COL_R_TIME + " INTEGER, " +
                COL_R_DATE + " TEXT)");

        insertDefaultQuestions(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        onCreate(db);
    }

    // ==================== USER METHODS ====================

    public long registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_U_USERNAME, username);
        cv.put(COL_U_EMAIL, email);
        cv.put(COL_U_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, cv);
        db.close();
        return result;
    }

    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_U_ID + " FROM " + TABLE_USERS +
                " WHERE " + COL_U_USERNAME + " = ?", new String[]{username});
        boolean exists = c != null && c.getCount() > 0;
        if (c != null) c.close();
        db.close();
        return exists;
    }

    public boolean isEmailTaken(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_U_ID + " FROM " + TABLE_USERS +
                " WHERE " + COL_U_EMAIL + " = ?", new String[]{email});
        boolean exists = c != null && c.getCount() > 0;
        if (c != null) c.close();
        db.close();
        return exists;
    }

    /**
     * Returns user ID if credentials match, -1 otherwise.
     */
    public int loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_U_ID + " FROM " + TABLE_USERS +
                        " WHERE " + COL_U_USERNAME + " = ? AND " + COL_U_PASSWORD + " = ?",
                new String[]{username, password});
        int userId = -1;
        if (c != null && c.moveToFirst()) {
            userId = c.getInt(0);
        }
        if (c != null) c.close();
        db.close();
        return userId;
    }

    public boolean updateUsername(int userId, String newUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_U_USERNAME, newUsername);
        int rows = db.update(TABLE_USERS, cv, COL_U_ID + " = ?",
                new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    public boolean updatePassword(int userId, String oldPassword, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Verify old password first
        Cursor c = db.rawQuery("SELECT " + COL_U_ID + " FROM " + TABLE_USERS +
                        " WHERE " + COL_U_ID + " = ? AND " + COL_U_PASSWORD + " = ?",
                new String[]{String.valueOf(userId), oldPassword});
        boolean valid = c != null && c.moveToFirst();
        if (c != null) c.close();

        if (!valid) {
            db.close();
            return false;
        }

        ContentValues cv = new ContentValues();
        cv.put(COL_U_PASSWORD, newPassword);
        db.update(TABLE_USERS, cv, COL_U_ID + " = ?",
                new String[]{String.valueOf(userId)});
        db.close();
        return true;
    }

    // ==================== RESULTS METHODS ====================

    public long saveResult(QuizResult result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_R_USER_ID, result.getUserId());
        cv.put(COL_R_CATEGORY, result.getCategory());
        cv.put(COL_R_SCORE, result.getScore());
        cv.put(COL_R_TOTAL, result.getTotalQuestions());
        cv.put(COL_R_TIME, result.getTimeTakenSeconds());
        cv.put(COL_R_DATE, result.getDateTime());
        long id = db.insert(TABLE_RESULTS, null, cv);
        db.close();
        return id;
    }

    public List<QuizResult> getResultsByUser(int userId) {
        List<QuizResult> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_RESULTS +
                        " WHERE " + COL_R_USER_ID + " = ? ORDER BY " + COL_R_ID + " DESC",
                new String[]{String.valueOf(userId)});

        if (c != null && c.moveToFirst()) {
            do {
                QuizResult r = new QuizResult();
                r.setId(c.getInt(c.getColumnIndexOrThrow(COL_R_ID)));
                r.setUserId(c.getInt(c.getColumnIndexOrThrow(COL_R_USER_ID)));
                r.setCategory(c.getString(c.getColumnIndexOrThrow(COL_R_CATEGORY)));
                r.setScore(c.getInt(c.getColumnIndexOrThrow(COL_R_SCORE)));
                r.setTotalQuestions(c.getInt(c.getColumnIndexOrThrow(COL_R_TOTAL)));
                r.setTimeTakenSeconds(c.getLong(c.getColumnIndexOrThrow(COL_R_TIME)));
                r.setDateTime(c.getString(c.getColumnIndexOrThrow(COL_R_DATE)));
                results.add(r);
            } while (c.moveToNext());
        }
        if (c != null) c.close();
        db.close();
        return results;
    }

    // ==================== QUESTIONS METHODS ====================

    public List<Question> getQuestionsByCategory(String category) {
        List<Question> questionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + COL_Q_CATEGORY + " = ?",
                new String[]{category});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Question question = new Question(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_QUESTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_OPTION1)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_OPTION2)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_OPTION3)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_OPTION4)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_Q_CORRECT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_CORRECT_TEXT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_TYPE)));
                questionList.add(question);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return questionList;
    }

    // ==================== DEFAULT QUESTIONS ====================

    private void addQuestion(SQLiteDatabase db, Question q) {
        ContentValues cv = new ContentValues();
        cv.put(COL_Q_QUESTION, q.getQuestion());
        cv.put(COL_Q_OPTION1, q.getOption1());
        cv.put(COL_Q_OPTION2, q.getOption2());
        cv.put(COL_Q_OPTION3, q.getOption3());
        cv.put(COL_Q_OPTION4, q.getOption4());
        cv.put(COL_Q_CORRECT, q.getCorrectAnswer());
        cv.put(COL_Q_CORRECT_TEXT, q.getCorrectAnswerText());
        cv.put(COL_Q_CATEGORY, q.getCategory());
        cv.put(COL_Q_TYPE, q.getQuestionType());
        db.insert(TABLE_QUESTIONS, null, cv);
    }

    private void insertDefaultQuestions(SQLiteDatabase db) {
        // PROGRAMMING
        addQuestion(db, new Question("What does HTML stand for?",
                "Hyper Text Markup Language", "Hyperlinks Text Markup Language",
                "Home Tool Markup Language", "None of these", 1, "Programming"));
        addQuestion(db, new Question("Which language is used for styling web pages?",
                "HTML", "JQuery", "CSS", "XML", 3, "Programming"));
        addQuestion(db, new Question("Java is a compiled language.", true, "Programming"));
        addQuestion(db, new Question("What is the extension of Java code files?", ".java", "Programming"));
        addQuestion(db, new Question("What does OOP stand for?",
                "Object Oriented Programming", "Object Oriented Protocol",
                "Open Object Protocol", "None of these", 1, "Programming"));

        // ADVANCED PROGRAMMING
        addQuestion(db, new Question("What is polymorphism in OOP?",
                "Multiple inheritance", "Method overloading and overriding",
                "Data hiding", "Code reuse", 2, "Advanced Programming"));
        addQuestion(db, new Question("An abstract class can be instantiated directly.", false, "Advanced Programming"));
        addQuestion(db, new Question("What design pattern ensures only one instance of a class?", "Singleton", "Advanced Programming"));
        addQuestion(db, new Question("Which keyword is used for inheritance in Java?",
                "implements", "extends", "inherits", "super", 2, "Advanced Programming"));
        addQuestion(db, new Question("What is the time complexity of binary search?",
                "O(n)", "O(n squared)", "O(log n)", "O(1)", 3, "Advanced Programming"));

        // DATA STRUCTURE
        addQuestion(db, new Question("Which data structure uses FIFO?",
                "Stack", "Queue", "Tree", "Graph", 2, "Data Structure"));
        addQuestion(db, new Question("A stack follows the LIFO principle.", true, "Data Structure"));
        addQuestion(db, new Question("What is the worst-case complexity of Quick Sort?",
                "O(n log n)", "O(n)", "O(n squared)", "O(log n)", 3, "Data Structure"));
        addQuestion(db, new Question("The data structure used for BFS traversal is called a ____.", "Queue", "Data Structure"));
        addQuestion(db, new Question("Which data structure is used for recursion?",
                "Queue", "Array", "Stack", "Linked List", 3, "Data Structure"));

        // MACHINE LEARNING
        addQuestion(db, new Question("What does CNN stand for in deep learning?",
                "Central Neural Network", "Convolutional Neural Network",
                "Connected Neural Network", "Computer Neural Network", 2, "Machine Learning"));
        addQuestion(db, new Question("Supervised learning requires labeled data.", true, "Machine Learning"));
        addQuestion(db, new Question("Which algorithm is used for classification?",
                "Linear Regression", "K-Means", "Decision Tree", "PCA", 3, "Machine Learning"));
        addQuestion(db, new Question("The technique of reducing overfitting by adding a penalty term is called ____.", "Regularization", "Machine Learning"));
        addQuestion(db, new Question("What is the purpose of an activation function?",
                "To store data", "To introduce non-linearity",
                "To reduce dimensions", "To normalize input", 2, "Machine Learning"));

        // DATABASE
        addQuestion(db, new Question("What does SQL stand for?",
                "Structured Query Language", "Simple Query Language",
                "Standard Query Logic", "Sequential Query Language", 1, "Database"));
        addQuestion(db, new Question("A primary key can contain NULL values.", false, "Database"));
        addQuestion(db, new Question("Which SQL command is used to retrieve data?",
                "GET", "FETCH", "SELECT", "RETRIEVE", 3, "Database"));
        addQuestion(db, new Question("The process of organizing data to reduce redundancy is called ____.", "Normalization", "Database"));
        addQuestion(db, new Question("Which type of join returns all rows from both tables?",
                "INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "FULL OUTER JOIN", 4, "Database"));

        // SOFTWARE DEVELOPMENT
        addQuestion(db, new Question("What does SDLC stand for?",
                "Software Development Life Cycle", "System Design Life Cycle",
                "Software Debugging Life Cycle", "System Development Logic Cycle", 1, "Software Development"));
        addQuestion(db, new Question("Agile methodology uses iterative development.", true, "Software Development"));
        addQuestion(db, new Question("Which model is also known as the linear sequential model?",
                "Agile", "Waterfall", "Spiral", "V-Model", 2, "Software Development"));
        addQuestion(db, new Question("A visual representation of a system's workflow is called a ____.", "Flowchart", "Software Development"));
        addQuestion(db, new Question("What is the purpose of version control?",
                "Track code changes", "Debug code", "Compile code", "Deploy code", 1, "Software Development"));

        // SOFTWARE TESTING
        addQuestion(db, new Question("What is black-box testing?",
                "Testing without seeing code", "Testing with source code",
                "Performance testing", "Security testing", 1, "Software Testing"));
        addQuestion(db, new Question("Unit testing tests individual components of software.", true, "Software Testing"));
        addQuestion(db, new Question("Which testing level tests the complete system?",
                "Unit Testing", "Integration Testing",
                "System Testing", "Acceptance Testing", 3, "Software Testing"));
        addQuestion(db, new Question("Testing done without executing the code is called ____ testing.", "Static", "Software Testing"));
        addQuestion(db, new Question("What type of testing checks non-functional requirements?",
                "Functional Testing", "Performance Testing",
                "Unit Testing", "Regression Testing", 2, "Software Testing"));

        // STATISTICS
        addQuestion(db, new Question("What is the mean of 2, 4, 6, 8, 10?",
                "5", "6", "7", "8", 2, "Statistics"));
        addQuestion(db, new Question("The median is always equal to the mean.", false, "Statistics"));
        addQuestion(db, new Question("Which measure of central tendency is most affected by outliers?",
                "Mean", "Median", "Mode", "Range", 1, "Statistics"));
        addQuestion(db, new Question("The measure of spread that uses squared deviations from the mean is called ____.", "Variance", "Statistics"));
        addQuestion(db, new Question("What does a p-value less than 0.05 typically indicate?",
                "No significance", "Statistical significance",
                "Random error", "Large sample size", 2, "Statistics"));
    }
}
