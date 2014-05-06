package com.example.hobbit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class RegistrationActivity extends Activity {

    private User hobbitUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        loginToSystem();
        goToMainMenu();
    }

    private void loginToSystem() {
        hobbitUser = (User) getIntent().getSerializableExtra("hobbitUser");
        Log.d("hobbit", "Logged in successfully as " + hobbitUser.getFirstname());
        String userId = hobbitUser.getUserId();
        if (isNotRegistered(userId)) {
            registerUser(userId);
        }
        login(userId);
    }

    private boolean isNotRegistered(String userId) {
        // TODO : implement isNotRegistered
        return true;
    }

    private void registerUser(String userId) {
        // TODO : implement registerUser
    }

    private void login(String userId){
        // TODO : implement login
    }
    private void goToMainMenu() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        if (hobbitUser != null) {
            intent.putExtra("hobbitUser", hobbitUser);
        }
        startActivity(intent);
    }
}
