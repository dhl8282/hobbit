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
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class RegistrationActivity extends Activity {

    private static final String TAG = "hobbit" + RegistrationActivity.class.getSimpleName();
    private User hobbitUser;
    private Database mongoDB = null;
    private DBCollection userCollection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginToSystem();
        UserCheckTask task = new UserCheckTask();
        task.execute();
        showMissionList();
    }

    private void loginToSystem() {
        hobbitUser = (User) getIntent().getSerializableExtra(Constants.USER_OBJECT);
        Log.d(TAG, "Logged in successfully as " + hobbitUser.getFirstname());
        AppPrefs appPrefs = new AppPrefs(getApplicationContext());
        appPrefs.setUserId(hobbitUser.getUserId()); 
    }

    private void showMissionList() {
        // TODO : rename EnterMissionActivity
        // TODO : delete MainMenuActivity
        Intent intent = new Intent(this, EnterMissionActivity.class);
        if (hobbitUser != null) {
            intent.putExtra(Constants.USER_OBJECT, hobbitUser);
        }
        startActivity(intent);
    }

    private class UserCheckTask extends AsyncTask<String, Void, Integer> {
        private int isRegistered(String userId) {
            mongoDB = new Database();
            if (mongoDB != null) {
                Log.d(TAG, "mongo db connected successfully");
                userCollection = mongoDB.getCollection(Database.COLLECTION_USER);
                BasicDBObject query = new BasicDBObject();
                query.put(Constants.USER_ID, userId);
                DBCursor cursor = userCollection.find(query);
                return cursor.length();
            }
            return 0;
        }
        
        @Override
        protected Integer doInBackground(String... params) {
            if (hobbitUser != null) {
                return isRegistered(hobbitUser.getUserId());
            } else {
                Log.d(TAG, "User object is null");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer userTotalLoginCount) {
            if (userTotalLoginCount > 0) {
                UpdateUserTask task = new UpdateUserTask();
                task.execute();
            } else {
                CreateUserTask task = new CreateUserTask();
                task.execute();
            }
        }
    }

    private class UpdateUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            incrementLoginCount(hobbitUser.getUserId());
            return null;
        }
    }
    
    private class CreateUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            createUserToDB(hobbitUser);
            return null;
        }
    }

    private void incrementLoginCount(String userId) {
        if (mongoDB != null && userCollection != null) {
            BasicDBObject query = new BasicDBObject();
            query.put(Constants.USER_ID, userId);
            BasicDBObject incValue = new BasicDBObject(Constants.USER_TOTAL_LOGIN, Constants.ONE);
            BasicDBObject intModifier = new BasicDBObject(Database.MONGODB_INCREMENT, incValue);
            userCollection.update(query, intModifier);
            Log.d(TAG, "Increment total login count for user");
        }
    }
    
    private void createUserToDB(User user) {
        if (mongoDB != null && user != null) {
            BasicDBObject document = new BasicDBObject();
            document.put(Constants.USER_ID, user.getUserId());
            document.put(Constants.USER_TOTAL_LOGIN, user.getTotalLogin());
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
