package com.example.quizapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.quizapp.R;
import com.example.quizapp.data.UserSession;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvGuestMessage = view.findViewById(R.id.tvGuestMessage);
        Button btnAuthAction = view.findViewById(R.id.btnAuthAction);

        UserSession session = new UserSession(getContext());
        
        if (session.isGuest()) {
            tvUsername.setText("Guest");
            tvGuestMessage.setVisibility(View.VISIBLE);
            btnAuthAction.setText("Create Account / Login");
            btnAuthAction.setOnClickListener(v -> {
                session.logout();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                if (getActivity() != null) {
                    getActivity().finish();
                }
            });
        } else {
            tvUsername.setText(session.getUsername());
            tvGuestMessage.setVisibility(View.GONE);
            btnAuthAction.setText("Logout");
            btnAuthAction.setOnClickListener(v -> {
                session.logout();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                if (getActivity() != null) {
                    getActivity().finish();
                }
            });
        }

        return view;
    }
}
