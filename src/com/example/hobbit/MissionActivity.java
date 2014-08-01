package com.example.hobbit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class MissionActivity extends Activity{

    private static final String TAG = "hobbit" + MissionActivity.class.getSimpleName();
    private TextView missionTitle, hintContent;
    private ImageView missionImage, startMissionButton, mapButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_detail);
        LinearLayout layout = (LinearLayout) findViewById(R.id.mission_layout);
        View itemView = LayoutInflater.from(this).inflate(R.layout.header_mission_detail, null, false);
        layout.addView(itemView);
        
        missionTitle = (TextView) findViewById(R.id.textview_header_mission_detail_title);
        hintContent = (TextView) findViewById(R.id.textview_header_mission_detail_hint);
        missionImage = (ImageView) findViewById(R.id.imageview_header_mission_detail_image);
        backButton = (ImageView) findViewById(R.id.button_mission_detail_back);

        Mission mission = (Mission) getIntent().getExtras().get(Constants.INTENT_EXTRA_MISSION);
        try {
            showMission(mission);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        addStartMissionButton(mission);
        addMapButton(mission);
        addBackButton();
    }

    private void addBackButton() {
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    private void addMissionImageZoom(final Bitmap bitmap) {
        final Intent intent = new Intent(this, ZoomImageActivity.class)
                .putExtra(Constants.INTENT_EXTRA_PHOTO_BITMAP, bitmap);
        
        missionImage.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                startActivity(intent);
            }
        });
    }
    
    private void addStartMissionButton(final Mission mission) {
        startMissionButton = (ImageView) findViewById(R.id.button_mission_detail_start);
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
        final Intent intent = new Intent(this, MapActivity.class)
                .putExtra(Constants.INTENT_EXTRA_MISSION, mission);
        mapButton = (ImageView) findViewById(R.id.button_header_mission_detail_share);
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
            if (scaledBitmap != null) {
                missionImage.setImageBitmap(scaledBitmap);
                addMissionImageZoom(scaledBitmap);
            }
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
                if (scaledBitmap != null) {
                    bmImage.setImageBitmap(scaledBitmap);
                    addMissionImageZoom(scaledBitmap);
                }
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
