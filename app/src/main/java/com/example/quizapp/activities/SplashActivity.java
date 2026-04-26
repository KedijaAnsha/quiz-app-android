package com.example.quizapp.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.data.UserSession;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvTitle = findViewById(R.id.tvSplashTitle);
        TextView tvSubtitle = findViewById(R.id.tvSplashSubtitle);

        // Initially invisible
        tvTitle.setAlpha(0f);
        tvTitle.setScaleX(0.5f);
        tvTitle.setScaleY(0.5f);
        tvSubtitle.setAlpha(0f);
        tvSubtitle.setTranslationY(30f);

        // Animate title: scale up + fade in
        ObjectAnimator titleFade = ObjectAnimator.ofFloat(tvTitle, View.ALPHA, 0f, 1f);
        titleFade.setDuration(800);

        ObjectAnimator titleScaleX = ObjectAnimator.ofFloat(tvTitle, View.SCALE_X, 0.5f, 1f);
        titleScaleX.setDuration(800);
        titleScaleX.setInterpolator(new OvershootInterpolator(1.5f));

        ObjectAnimator titleScaleY = ObjectAnimator.ofFloat(tvTitle, View.SCALE_Y, 0.5f, 1f);
        titleScaleY.setDuration(800);
        titleScaleY.setInterpolator(new OvershootInterpolator(1.5f));

        AnimatorSet titleSet = new AnimatorSet();
        titleSet.playTogether(titleFade, titleScaleX, titleScaleY);
        titleSet.setStartDelay(300);
        titleSet.start();

        // Animate subtitle: slide up + fade in
        ObjectAnimator subtitleFade = ObjectAnimator.ofFloat(tvSubtitle, View.ALPHA, 0f, 1f);
        subtitleFade.setDuration(600);

        ObjectAnimator subtitleSlide = ObjectAnimator.ofFloat(tvSubtitle, View.TRANSLATION_Y, 30f, 0f);
        subtitleSlide.setDuration(600);

        AnimatorSet subtitleSet = new AnimatorSet();
        subtitleSet.playTogether(subtitleFade, subtitleSlide);
        subtitleSet.setStartDelay(900);
        subtitleSet.start();

        // Navigate after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            UserSession session = new UserSession(SplashActivity.this);
            Intent intent;
            if (session.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2500);
    }
}
