package com.example.quizapp.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.data.DBHelper;
import com.example.quizapp.data.Question;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private String category;

    private TextView tvTimer, tvQuestion;

    private MaterialButton btnOption1, btnOption2, btnOption3, btnOption4;
    private MaterialButton btnExplain, btnNext;

    private int currentQuestionIndex = 0;
    private int score = 0;
    private int selectedOption = -1;

    private DBHelper dbHelper;
    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        category = getIntent().getStringExtra("category");
        if (category == null) category = "Programming";

        initViews();

        dbHelper = new DBHelper(this);
        questionList = dbHelper.getQuestionsByCategory(category);

        if (questionList != null && !questionList.isEmpty()) {
            displayQuestion();
        } else {
            Toast.makeText(this, "No questions available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestion = findViewById(R.id.tvQuestion);

        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);

        btnExplain = findViewById(R.id.btnExplain);
        btnNext = findViewById(R.id.btnNext);

        setupListeners();
    }

    private void setupListeners() {
        btnOption1.setOnClickListener(v -> selectOption(1, btnOption1));
        btnOption2.setOnClickListener(v -> selectOption(2, btnOption2));
        btnOption3.setOnClickListener(v -> selectOption(3, btnOption3));
        btnOption4.setOnClickListener(v -> selectOption(4, btnOption4));

        btnNext.setOnClickListener(v -> goToNextQuestion());

        btnExplain.setOnClickListener(v -> {
            Toast.makeText(this, "Explanation feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void selectOption(int option, MaterialButton selectedButton) {
        selectedOption = option;
        resetOptionStyles();

        selectedButton.setBackgroundTintList(getColorStateList(R.color.purple_200));
    }

    private void resetOptionStyles() {
        btnOption1.setBackgroundTintList(null);
        btnOption2.setBackgroundTintList(null);
        btnOption3.setBackgroundTintList(null);
        btnOption4.setBackgroundTintList(null);
    }

    private void goToNextQuestion() {
        if (selectedOption == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        Question currentQuestion = questionList.get(currentQuestionIndex);

        if (selectedOption == currentQuestion.getCorrectAnswer()) {
            score++;
        }

        currentQuestionIndex++;
        selectedOption = -1;

        if (currentQuestionIndex < questionList.size()) {
            displayQuestion();
        } else {
            showResult();
        }
    }

    private void displayQuestion() {
        Question currentQuestion = questionList.get(currentQuestionIndex);

        tvQuestion.setText(currentQuestion.getQuestion());
        btnOption1.setText(currentQuestion.getOption1());
        btnOption2.setText(currentQuestion.getOption2());
        btnOption3.setText(currentQuestion.getOption3());
        btnOption4.setText(currentQuestion.getOption4());

        resetOptionStyles();
    }

    private void showResult() {
        Toast.makeText(this, "Score: " + score + "/" + questionList.size(), Toast.LENGTH_LONG).show();

        // Later: move to ResultActivity
        finish();
    }
}