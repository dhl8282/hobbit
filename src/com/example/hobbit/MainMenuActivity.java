package com.example.hobbit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends BaseActivity {

    private static final String TAG = "hobbit" + MainMenuActivity.class.getSimpleName();
    private Button buttonCreateMission, buttonEnterMission;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        addCreateMission();
        addEnterMission();
    }

    private void addCreateMission() {
        final Intent prepareCreateMissionIntent = new Intent(this, PrepareCreateMissionActivity.class);
        buttonCreateMission = (Button) findViewById(R.id.buttonCreateMission);
        buttonCreateMission.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("hobbit", "Ereate mission button is clicked");
                startActivity(prepareCreateMissionIntent);
            }
        });
    }

    private void addEnterMission() {
        final Intent enterMissionIntent = new Intent(this, EnterMissionActivity.class);
        buttonEnterMission = (Button) findViewById(R.id.buttonEnterMission);
        buttonEnterMission.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("hobbit", "Enter mission button is clicked");
                startActivity(enterMissionIntent);
            }
        });
    }

}
