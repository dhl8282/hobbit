package com.example.hobbit;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class MainMenuActivity extends Activity {

    private static final String TAG = "hobbit" + MainMenuActivity.class.getSimpleName();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button buttonCreateMission, buttonEnterMission;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		addCreateMission();
		addEnterMission();
	}

//	@Override
//	public void onBackPressed() {
//	    // TODO Auto-generated method stub
//	    super.onBackPressed();
//	    Intent intent = new Intent(this, MainMenuActivity.class);
//        startActivity(intent);
//	}

	private void addCreateMission() {
		buttonCreateMission = (Button) findViewById(R.id.buttonCreateMission);
		buttonCreateMission.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			    Log.d("hobbit", "create mission button is clicked");
			    dispatchTakePictureIntent();
			}
		});
	}

	private void addEnterMission() {
	    buttonEnterMission = (Button) findViewById(R.id.buttonEnterMission);
	    buttonEnterMission.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showMap();
            }
        });
	}

	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    //Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        Intent intent = new Intent(this, CreateMissionActivity.class);
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        intent.putExtra("picture", imageBitmap);
	        startActivity(intent);
	    }
	}

	private void showMap() {
	    setContentView(R.layout.map_fragment);
	    GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

	    GPSTracker gps = new GPSTracker(this);
	    double latitude = 0;
	    double longitude = 0;

        // check if GPS enabled
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            gps.showSettingsAlert();
        }

        LatLng currentLocation = new LatLng(latitude, longitude);

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

        GetMissionsTask task = new GetMissionsTask(map);
        task.execute();
	}

	private List<BasicDBObject> showMissions(GoogleMap map) {
	    Database mongoDB = new Database();
	    List<BasicDBObject> missions = new ArrayList<BasicDBObject>();
        if (mongoDB != null) {
            DBCollection collection = mongoDB.getCollection(Database.COLLECTION_PARENT_MISSION);
            Log.d(TAG, "mongo db collection connected successfully");
            DBCursor cursor = collection.find();
            while (cursor.hasNext()) {
                BasicDBObject obj = (BasicDBObject) cursor.next();
                missions.add(obj);
            }
        }
        return missions;
	}

	private class GetMissionsTask extends AsyncTask<String, Void, List<BasicDBObject>> {
	    GoogleMap mMap;
        public GetMissionsTask(GoogleMap map) {
            mMap = map;
        }

        @Override
        protected List<BasicDBObject> doInBackground(String... params) {
            return showMissions(mMap);
        }

        @Override
        protected void onPostExecute(List<BasicDBObject> missions) {
          Log.d(TAG, "total mission is " + missions.size());
          for (BasicDBObject obj : missions) {
              String lat = obj.get("lat").toString();
              String lng = obj.get("lng").toString();
              LatLng mark = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
              mMap.addMarker(new MarkerOptions()
                      .position(mark)
                      .title(obj.get("title").toString())
                      .snippet(obj.get("hint").toString()));
          }
        }
    }

}
