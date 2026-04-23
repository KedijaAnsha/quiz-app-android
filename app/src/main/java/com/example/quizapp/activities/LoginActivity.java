package com.example.quizapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.data.DBHelper;
import com.example.quizapp.data.UserSession;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        TextView btnLogin = findViewById(R.id.btnLogin);
        TextView btnGuest = findViewById(R.id.btnGuest);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());

        btnGuest.setOnClickListener(v -> {
            UserSession session = new UserSession(LoginActivity.this);
            session.createGuestSession();
            goToHome();
        });

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            showError("Please enter your username");
            return;
        }
        if (password.isEmpty()) {
            showError("Please enter your password");
            return;
        }

        DBHelper db = new DBHelper(this);
        int userId = db.loginUser(username, password);

        if (userId != -1) {
            UserSession session = new UserSession(this);
            session.createLoginSession(userId, username);
            goToHome();
        } else {
            showError("Invalid username or password");
        }
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }

    private void goToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
