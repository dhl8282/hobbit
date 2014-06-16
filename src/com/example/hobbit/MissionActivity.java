package com.example.hobbit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hobbit.util.Constants;
import com.example.hobbit.util.GPSTracker;
import com.example.hobbit.util.ImageProcess;
import com.example.hobbit.util.Mission;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class MissionActivity extends Activity {

	private static final String TAG = "hobbit" + MissionActivity.class.getSimpleName();
    private TextView missionTitle, hintContent;
    private ImageView missionImage;
    private Button startMissionButton, mapButton;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        missionTitle = (TextView) findViewById(R.id.textViewMissionTitle);
        hintContent = (TextView) findViewById(R.id.textViewHintContent);
        missionImage = (ImageView) findViewById(R.id.imageViewMissionImage);

        Mission mission = (Mission) getIntent().getExtras().get(Constants.INTENT_EXTRA_MISSION);
        try {
			showMission(mission);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        addStartMissionButton(mission);
        addMapButton(mission);
    }

    private void addStartMissionButton(final Mission mission) {
    	startMissionButton = (Button) findViewById(R.id.buttonStart);
    	startMissionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final Intent intent = new Intent(getApplicationContext(), PrepareCreateMissionActivity.class);
				intent.putExtra(Constants.INTENT_EXTRA_MISSION, mission);
				startActivity(intent);
			}
		});
    }
    
    private void addMapButton(final Mission mission) {
    	mapButton = (Button) findViewById(R.id.buttonMap);
    	mapButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showInTheMap(mission);
			}
		});
    }

    private void showMission(Mission mission) throws IOException {
    	if (mission != null) {
	    	missionTitle.setText(mission.getTitle());
	    	hintContent.setText(mission.getHint());

	    	if (!mission.getLocalPhotoPath().isEmpty()) {
	    		showPhotoFromLocal(mission.getLocalPhotoPath());
	    	} else {
	    		showPhotoFromUrl(mission.getPhotoUrl());
	    	}
    	}
    }

    private void showPhotoFromLocal(String localPath) {
    	File imgFile = new  File(localPath);
    	if(imgFile.exists()){
    	    Bitmap scaledBitmap = ImageProcess.getBitmapFromMemCache(localPath);
    	    missionImage.setImageBitmap(scaledBitmap);
    	}
    }

    private void showPhotoFromUrl(String url) {
    	new DownloadImageTask(missionImage).execute(url);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ProgressDialog dialog;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected void onPreExecute() {
            dialog = new ProgressDialog(MissionActivity.this);
            dialog.setMessage(MissionActivity.this
                    .getString(R.string.loading));
            dialog.setCancelable(false);
            dialog.show();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
        	if (result != null) {
        		Bitmap scaledBitmap = Bitmap.createScaledBitmap(result, 200, 300, true);
        		bmImage.setImageBitmap(scaledBitmap);
        	}
            dialog.dismiss();
        }
    }
    
    private LatLng getCurrentLocation() {
    	GPSTracker gps = new GPSTracker(this);
        double latitude = 0;
        double longitude = 0;

        // check if GPS enabled
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            gps.showSettingsAlert();
        }

        return new LatLng(latitude, longitude);
    }
    
    private void showInTheMap(Mission mission) {
        setContentView(R.layout.map_fragment);
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        LatLng currentLocation = getCurrentLocation();

        if (map != null){
            Marker markMarker = map.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("You"));
        }

        if (currentLocation != null) {
            //Move the camera instantly to mark with a zoom of 12.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
        }
        makeMissionMarkersInMap(map, mission);
    }
    
    private void makeMissionMarkersInMap(GoogleMap map, Mission mission) {
    	LatLng mark = new LatLng(mission.getLatitude(), mission.getLongitude());
        map.addMarker(new MarkerOptions()
                .position(mark)
                .title(mission.getTitle())
                .snippet(mission.getHint()));
    
    }
    
}
