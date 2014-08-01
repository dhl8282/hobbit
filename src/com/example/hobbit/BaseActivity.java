package com.example.hobbit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends Activity {
     
    private static final String TAG = "hobbit" + BaseActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.action_validate:
            callValidation();
            return true;
        case R.id.action_add_mission:
            addMission();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void addMission() {
        Log.d(TAG, "Add mission is caled");
        Intent intent = new Intent(this, PrepareCreateMissionActivity.class);
        startActivity(intent);
    }
    
    private void callValidation() {
        Log.d(TAG, "Validation is caled");
        Intent intent = new Intent(this, ValidateActivity.class);
        startActivity(intent);
    }
}