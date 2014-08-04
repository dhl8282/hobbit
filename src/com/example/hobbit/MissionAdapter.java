package com.example.hobbit;

import java.util.List;

import com.example.hobbit.util.Mission;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class MissionAdapter extends ArrayAdapter<Mission> {
    
    public MissionAdapter(Context c, List<Mission> missions) {
        super(c, 0, missions);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MissionView missionView = (MissionView)convertView;
        if (null == missionView)
            missionView = MissionView.inflate(parent);
        missionView.setMission(getItem(position));
        return missionView;
    }

}
