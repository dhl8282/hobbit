package com.example.hobbit;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.hobbit.util.AppPrefs;
import com.example.hobbit.util.Constants;
import com.example.hobbit.util.Database;
import com.example.hobbit.util.User;
import com.mongodb.DBCollection;

public class RegistrationActivity extends Activity {

    private static final String TAG = "hobbit" + RegistrationActivity.class.getSimpleName();
    private User hobbitUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        VerifyUserTask task = new VerifyUserTask();
        task.execute();
        loginToSystem();
        goToMainMenu();
    }

    private void loginToSystem() {
        hobbitUser = (User) getIntent().getSerializableExtra(Constants.USER_OBJECT);
        Log.d(TAG, "Logged in successfully as " + hobbitUser.getFirstname());
        String userId = hobbitUser.getUserId();
        if (isNotRegistered(userId)) {
            registerUser(userId);
            AppPrefs appPrefs = new AppPrefs(getApplicationContext());
            appPrefs.setUser_id(userId);
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
            intent.putExtra(Constants.USER_OBJECT, hobbitUser);
        }
        startActivity(intent);
    }

    private class VerifyUserTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String inputId = "";
            String inputPwd = "";
            verifyIdPwd(inputId, inputPwd);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
//          textView.setText(result);
        }
    }

    private void verifyIdPwd(String inputId, String inputPwd) {
        Database mongoDB = new Database();
        if (mongoDB != null) {
            DBCollection coll = mongoDB.getCollection(Database.COLLECTION_USER);
            Log.d(TAG, "mongo db collection connected successfully");
        }
    }
}
