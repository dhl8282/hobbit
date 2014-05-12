package com.example.hobbit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class CreateMissionActivity extends Activity {

    private static final String TAG = "hobbit" + CreateMissionActivity.class.getSimpleName();
    private GPSTracker gps;
    private ImageView imageViewPicPreview;
    private EditText editTextMissionTitle, editTextHint;
    private Button buttonTouchMe;
    private String missionTitle, hint;
    private double longitude, latitude;
    private GoogleMap map;
    private Bitmap imageBitmap;
    private Mission missionItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_preview);
        showPic();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    private void showPic() {
        imageViewPicPreview = (ImageView) findViewById(R.id.imageViewPicPreview);
        editTextMissionTitle = (EditText) findViewById(R.id.editTextMissionTitle);
        editTextHint = (EditText) findViewById(R.id.editTextHint);
        buttonTouchMe = (Button) findViewById(R.id.buttonTouchMe);
        Intent intent = getIntent();
        imageBitmap = (Bitmap) intent.getExtras().get("picture");
        imageViewPicPreview.setImageBitmap(imageBitmap);

        buttonTouchMe.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setContentView(R.layout.map_fragment);
                missionTitle = editTextMissionTitle.getText().toString();
                hint = editTextHint.getText().toString();
                getGPSLocation();
                AppPrefs appPrefs = new AppPrefs(getApplicationContext());
                String userId = appPrefs.getUser_id();
                missionItem = new Mission(missionTitle, hint, longitude, latitude);
                missionItem.setUserId(userId);
                CreateMissionTask task = new CreateMissionTask(missionItem);
                task.execute();
                // TODO : image process in DB
                showPicInGoogleMap(imageBitmap, missionItem);
            }
        });
    }

    private void getGPSLocation() {
        gps = new GPSTracker(this);

        // check if GPS enabled
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.d(TAG, "lat is " + latitude);
            Log.d(TAG, "long is " + longitude);
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    private void showPicInGoogleMap(Bitmap imageBitmap, Mission mission) {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        LatLng mark = null;
        if (map != null && mission != null){
            mark = mission.getMark();
            Marker markMarker = map.addMarker(new MarkerOptions()
                .position(mark)
                .title(mission.getTitle())
                .snippet(mission.getHint())
                .icon(BitmapDescriptorFactory
                        .fromBitmap(imageBitmap)));
        }

        if (mark != null) {
            //Move the camera instantly to mark with a zoom of 15.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mark, 18));

            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
        }
    }

    private class CreateMissionTask extends AsyncTask<String, Void, String> {
        private Mission mMission;

        public CreateMissionTask(Mission mission) {
            mMission = mission;
        }

        @Override
        protected String doInBackground(String... params) {
            createMissionToDB(mMission);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
//          textView.setText(result);
        }
    }

    private void createMissionToDB(Mission mission) {
        Database mongoDB = new Database();
        Log.d(TAG, "mongo db connected successfully");
        if (mongoDB != null && mission != null) {
            DBCollection collection = mongoDB.getCollection(Database.COLLECTION_PARENT_MISSION);
            BasicDBObject document = new BasicDBObject();
            document.put("userId", mission.getUserId());
            document.put("title", mission.getTitle());
            document.put("hint", mission.getHint());
            double[] loc = {mission.getLatitude(), mission.getLongitude()};
            document.put("loc", loc);
//            document.put("lng", mission.getLongitude());
//            document.put("lat", mission.getLatitude());
            collection.insert(document);
            Log.d(TAG, "Mission is created in DB");
        }
    }
}
