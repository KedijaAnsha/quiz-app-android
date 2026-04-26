package com.example.quizapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quizapp.R;

public class HomeFragment extends Fragment {

    private final String[] categories = {
            "Programming", "Advanced Programming", "Data Structure", "Machine Learning",
            "Database", "Software Development", "Software Testing", "Statistics"
    };

    private final int[] cardIds = {
            R.id.cardProgramming, R.id.cardAdvProgramming,
            R.id.cardDataStructure, R.id.cardML,
            R.id.cardDatabase, R.id.cardSoftwareDev,
            R.id.cardSoftwareTest, R.id.cardStatistics
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        for (int i = 0; i < cardIds.length; i++) {
            final String category = categories[i];
            View card = view.findViewById(cardIds[i]);

            // Animate cards appearing
            card.setAlpha(0f);
            card.setTranslationY(50f);
            card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setStartDelay(100L * i)
                    .start();

            card.setOnClickListener(v -> {
                // Scale animation on click
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    Intent intent = new Intent(getActivity(), QuizActivity.class);
                    intent.putExtra("category", category);
                    startActivity(intent);
                }).start();
            });
        }

        return view;
    }
}
