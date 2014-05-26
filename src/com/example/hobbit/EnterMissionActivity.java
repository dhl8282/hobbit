package com.example.hobbit;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hobbit.util.Constants;
import com.example.hobbit.util.Database;
import com.example.hobbit.util.GPSTracker;
import com.example.hobbit.util.Mission;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class EnterMissionActivity extends Activity {

    private static final String TAG = "hobbit" + EnterMissionActivity.class.getSimpleName();
    private LinearLayout mainPageLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        showMap();
        showMissionsInList();
    }
    
    private void startMissionActivity(BasicDBObject obj) {
    	Mission mission = makeMissionFromDB(obj);
        Intent intent = new Intent(this, MissionActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_MISSION, mission);
        startActivity(intent);
    }
    
    private Mission makeMissionFromDB(BasicDBObject obj) {
    	String title = "";
    	String hint = "";
    	String id = "";
    	Double lng, lat;
		if (obj.get(Constants.MISSON_TITLE) != null) {
			title = obj.get(Constants.MISSON_TITLE).toString();
		}
		if (obj.get(Constants.MISSON_HINT) != null) {
			hint = obj.get(Constants.MISSON_HINT).toString();
		}
		
		BasicDBList loc = (BasicDBList)obj.get(Constants.MISSON_LOC);
		lng = (Double) loc.get(0);
		lat = (Double) loc.get(1);
		
		id = obj.get(Constants.MISSON_MONGO_DB_ID).toString();
		
		Mission mission = new Mission(title, hint, lng, lat);
		mission.setPhotoUrl(Constants.makeUrl(id));
		
		return mission;
    }

    private void showMissionsInList() {
    	setContentView(R.layout.main_page);
    	mainPageLinearLayout = (LinearLayout) findViewById(R.id.mainPageLinearLayout);
    	LatLng currentLocation = getCurrentLocation();
    	GetMissionsTask task = new GetMissionsTask(this, currentLocation);
        task.execute();
    }
    
    private void showMap() {
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

        GetMissionsTask task = new GetMissionsTask(map, currentLocation);
        task.execute();
    }

    private LatLng getCurrentLocation() {
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

        return new LatLng(latitude, longitude);
    }
    
    private List<BasicDBObject> showMissions(GoogleMap map, LatLng currentLoc, int missionNumber) {
        Database mongoDB = new Database();
        List<BasicDBObject> missions = new ArrayList<BasicDBObject>();
        if (mongoDB != null) {
            DBCollection collection = mongoDB.getCollection(Database.COLLECTION_PARENT_MISSION);
            Log.d(TAG, "mongo db collection connected successfully");
            double[] loc = {currentLoc.latitude , currentLoc.longitude};
            // Limit to 10 results
            DBCursor cursor = collection.find( new BasicDBObject( "loc", new BasicDBObject("$near", loc))).limit(missionNumber);
            while (cursor.hasNext()) {
                BasicDBObject obj = (BasicDBObject) cursor.next();
                missions.add(obj);
            }
        }
        Log.d(TAG, "Total " + missions.size() + " missions are returned");
        return missions;
    }

    private class GetMissionsTask extends AsyncTask<String, Void, List<BasicDBObject>> {
        GoogleMap mMap;
        LatLng mCurrentLoc;
        Context mContext;
        ProgressDialog dialog;
        
        public GetMissionsTask(Context context, LatLng currentLoc) {
        	mContext = context;
        	mCurrentLoc = currentLoc;
        }
        
        public GetMissionsTask(GoogleMap map, LatLng currentLoc) {
            mMap = map;
            mCurrentLoc = currentLoc;
        }

        protected void onPreExecute() {
            dialog = new ProgressDialog(EnterMissionActivity.this);
            dialog.setMessage(EnterMissionActivity.this
                    .getString(R.string.uploading));
            dialog.setCancelable(false);
            dialog.show();
        }
        
        @Override
        protected List<BasicDBObject> doInBackground(String... params) {
            return showMissions(mMap, mCurrentLoc, Constants.DEFAULT_MISSION_NUMBER);
        }

        @Override
        protected void onPostExecute(List<BasicDBObject> missions) {
            if (missions == null) {
                Log.d(TAG, "No missions are found");
            }
            else {
//            		makeMissionMarkersInMap(mMap, missions);
            	makeMissionList(missions);
            }
            dialog.dismiss();
        }
        
        private void makeMissionList(List<BasicDBObject> missions) {
        	if (missions.size() == 0) {
        		Log.d(TAG, "No mission returned");
        		return;
        	}
        	
//        	final TextView[] myTextViews = new TextView[missions.size()];
        	for (final BasicDBObject obj : missions) {
				final TextView rowTextView = new TextView(mContext);
				rowTextView.setPadding(50, 100, 0, 0);
				
				String title = "Title is empty";
				if (obj.get(Constants.MISSON_TITLE) != null) {
					title = obj.get(Constants.MISSON_TITLE).toString();
				}

				title += "_" + obj.get(Constants.MISSON_MONGO_DB_ID).toString(); 
				rowTextView.setText(title);
				rowTextView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						startMissionActivity(obj);
					}
				});

			    // add the textview to the linearlayout
				mainPageLinearLayout.addView(rowTextView);

			}
        	
        }
        
        private void makeMissionMarkersInMap(GoogleMap map, List<BasicDBObject> missions) {
        	Log.d(TAG, "total mission is " + missions.size());
            for (BasicDBObject obj : missions) {
                BasicDBList loc = (BasicDBList) obj.get(Constants.MISSON_LOC);
                String lat = loc.get(0).toString();
                String lng = loc.get(1).toString();
                LatLng mark = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                map.addMarker(new MarkerOptions()
                        .position(mark)
                        .title(obj.get(Constants.MISSON_TITLE).toString())
                        .snippet(obj.get(Constants.MISSON_HINT).toString()));
            }
        }
    }
}
