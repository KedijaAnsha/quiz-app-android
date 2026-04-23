package com.example.quizapp.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages user session using SharedPreferences.
 * Stores login state, user ID, username, and guest mode flag.
 */
public class UserSession {

    private static final String PREF_NAME = "QuizzySession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_GUEST = "isGuest";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public UserSession(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createLoginSession(int userId, String username) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_IS_GUEST, false);
        editor.apply();
    }

    public void createGuestSession() {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, -1);
        editor.putString(KEY_USERNAME, "Guest");
        editor.putBoolean(KEY_IS_GUEST, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isGuest() {
        return prefs.getBoolean(KEY_IS_GUEST, false);
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "Guest");
    }

    public void updateUsername(String newUsername) {
        editor.putString(KEY_USERNAME, newUsername);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
