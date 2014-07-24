package com.example.hobbit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.hobbit.util.AppPrefs;
import com.example.hobbit.util.Constants;
import com.example.hobbit.util.Database;
import com.example.hobbit.util.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class RegistrationActivity extends BaseActivity {

    private static final String TAG = "hobbit" + RegistrationActivity.class.getSimpleName();
    private User hobbitUser;
    private Database mongoDB = null;
    private DBCollection userCollection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginToSystem();
//        VerifyUserTask task = new VerifyUserTask();
//        task.execute();
//        CreateUserTask task = new CreateUserTask();
//        task.execute();
        goToMainMenu();
    }

    private void loginToSystem() {
        hobbitUser = (User) getIntent().getSerializableExtra(Constants.USER_OBJECT);
        Log.d(TAG, "Logged in successfully as " + hobbitUser.getFirstname());
        String userId = hobbitUser.getUserId();
        if (isNotRegistered(userId)) {
            registerUser(userId);
            AppPrefs appPrefs = new AppPrefs(getApplicationContext());
            appPrefs.setUserId(userId);
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
//        Intent intent = new Intent(this, MainMenuActivity.class);
    	Intent intent = new Intent(this, EnterMissionActivity.class);
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

    private class CreateUserTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
        	createUserToDB(hobbitUser);
            return null;
        }
    }

    private void createUserToDB(User user) {
        mongoDB = new Database();
        Log.d(TAG, "mongo db connected successfully");
        if (mongoDB != null && user != null) {
            userCollection = mongoDB.getCollection(Database.COLLECTION_USER);

            BasicDBObject document = new BasicDBObject();
            document.put(Constants.USER_ID, user.getUserId());
            document.put(Constants.USER_LAST_NAME, user.getLastname());
            document.put(Constants.USER_FIRST_NAME, user.getFirstname());
            document.put(Constants.USER_EMAIL, user.getEmail());
            document.put(Constants.USER_GENDER, user.getGender());

            String[] createdMission = {};
            String[] repliedMission = {};
            String[] succeedMission = {};
            String[] failedMission = {};

            document.put(Constants.USER_CREATED_MISSIONS, createdMission);
            document.put(Constants.USER_REPLIED_MISSIONS, repliedMission);
            document.put(Constants.USER_SUCCEED_MISSIONS, succeedMission);
            document.put(Constants.USER_FAILED_MISSIONS, failedMission);
            userCollection.insert(document);
        }
    }
}
