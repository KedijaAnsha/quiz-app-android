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

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvError = findViewById(R.id.tvError);
        TextView btnRegister = findViewById(R.id.btnRegister);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());

        tvGoToLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate username - no spaces
        if (username.isEmpty()) {
            showError("Please enter a username");
            return;
        }
        if (username.contains(" ")) {
            showError("Username cannot contain spaces");
            return;
        }
        if (username.length() < 3) {
            showError("Username must be at least 3 characters");
            return;
        }

        // Validate email
        if (email.isEmpty()) {
            showError("Please enter your email");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email");
            return;
        }

        // Validate password - min 6 digits
        if (password.isEmpty()) {
            showError("Please enter a password");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        // Confirm password
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        DBHelper db = new DBHelper(this);

        // Check if username is taken
        if (db.isUsernameTaken(username)) {
            showError("Username is already taken");
            return;
        }

        // Check if email is taken
        if (db.isEmailTaken(email)) {
            showError("Email is already registered");
            return;
        }

        // Register user
        long result = db.registerUser(username, email, password);
        if (result != -1) {
            UserSession session = new UserSession(this);
            session.createLoginSession((int) result, username);

            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }
}
