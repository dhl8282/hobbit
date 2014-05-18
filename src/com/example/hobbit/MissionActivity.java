package com.example.hobbit;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hobbit.util.Constants;
import com.example.hobbit.util.Mission;

public class MissionActivity extends Activity {

    private TextView missionTitle;
    private ImageView missionImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        missionTitle = (TextView) findViewById(R.id.textViewMissionTitle);
        missionImage = (ImageView) findViewById(R.id.imageViewMissionImage);
        Mission mission = (Mission) getIntent().getExtras().get(Constants.INTENT_GET_MISSION);
        showMission(mission);
    }

    private void showMission(Mission mission) {

    }
}
