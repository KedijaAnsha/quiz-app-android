package com.example.quizapp.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.quizapp.R;
import com.example.quizapp.data.DBHelper;
import com.example.quizapp.data.QuizResult;
import com.example.quizapp.data.UserSession;
import java.util.List;

public class ResultsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        LinearLayout containerLayout = view.findViewById(R.id.llResultsContainer);

        UserSession session = new UserSession(getContext());
        if (session.isGuest()) {
            TextView tv = new TextView(getContext());
            tv.setText("Guest users do not have saved results. Please login or create an account to track your progress.");
            tv.setTextColor(0xFFB8B8D0);
            tv.setTextSize(16f);
            containerLayout.addView(tv);
            return view;
        }

        DBHelper db = new DBHelper(getContext());
        List<QuizResult> results = db.getResultsByUser(session.getUserId());

        if (results.isEmpty()) {
            TextView tv = new TextView(getContext());
            tv.setText("No results found. Take a quiz!");
            tv.setTextColor(0xFFB8B8D0);
            tv.setTextSize(16f);
            containerLayout.addView(tv);
        } else {
            for (QuizResult r : results) {
                View card = inflater.inflate(R.layout.item_result_card, containerLayout, false);
                TextView tvCat = card.findViewById(R.id.tvCategory);
                TextView tvScore = card.findViewById(R.id.tvScore);
                TextView tvDate = card.findViewById(R.id.tvDate);
                
                tvCat.setText(r.getCategory());
                tvScore.setText("Score: " + r.getScore() + " / " + r.getTotalQuestions());
                tvDate.setText(r.getDateTime());
                
                containerLayout.addView(card);
            }
        }

        return view;
    }
}
