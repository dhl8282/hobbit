package com.example.hobbit;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

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
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
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
    private Database mongoDB = null;
    private DBCollection parentCollection = null;

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
    	String userId = "";
    	String missionId = "";
    	Double lng, lat;
		if (obj.get(Constants.MISSON_TITLE) != null) {
			title = obj.get(Constants.MISSON_TITLE).toString();
		}
		if (obj.get(Constants.MISSON_HINT) != null) {
			hint = obj.get(Constants.MISSON_HINT).toString();
		}
		if (obj.get(Constants.USER_ID) != null) {
			userId = obj.get(Constants.USER_ID).toString();
		}

		BasicDBList loc = (BasicDBList)obj.get(Constants.MISSON_LOC);
		lng = (Double) loc.get(0);
		lat = (Double) loc.get(1);

		missionId = obj.get(Constants.MISSON_MONGO_DB_ID).toString();
		UpdateMissionTask task = new UpdateMissionTask();
		task.execute(missionId);

		Mission mission = new Mission(title, hint, lng, lat);
		mission.setMissionId(missionId);
		mission.setPhotoUrl(Constants.makeOriginalUrl(missionId));
		mission.setThumnailUrl(Constants.makeThumnailUrl(missionId));
		mission.setUserId(userId);

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
        mongoDB = new Database();
        List<BasicDBObject> missions = new ArrayList<BasicDBObject>();
        if (mongoDB != null) {
            parentCollection = mongoDB.getCollection(Database.COLLECTION_PARENT_MISSION);
            Log.d(TAG, "mongo db collection connected successfully");
            double[] loc = {currentLoc.latitude , currentLoc.longitude};
            // Limit to 10 results
            DBCursor cursor = parentCollection.find( new BasicDBObject( "loc", new BasicDBObject("$near", loc))).limit(missionNumber);
            while (cursor.hasNext()) {
                BasicDBObject obj = (BasicDBObject) cursor.next();
                missions.add(obj);
            }
        }
        Log.d(TAG, "Total " + missions.size() + " missions are returned");
        return missions;
    }

    private class UpdateMissionTask extends AsyncTask<String, Void, String> {

    	private void updateParentMissionToDB(String missionId) {
        	if (parentCollection == null) {
        		return;
        	}
    	    BasicDBObject query = new BasicDBObject();
    	    query.put(Constants.MISSON_MONGO_DB_ID, new ObjectId(missionId));
    	    BasicDBObject incValue = new BasicDBObject(Constants.MISSON_COUNT_VIEW, Constants.ONE);
    	    BasicDBObject intModifier = new BasicDBObject(Database.MONGODB_INCREMENT, incValue);
    	    parentCollection.update(query, intModifier);
        }

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "Mission to be updated is " + params[0]);
			updateParentMissionToDB(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "Parent mission view count is updated");
			super.onPostExecute(result);
		}
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

        	for (final BasicDBObject obj : missions) {
        	    final LinearLayout layout = new LinearLayout(mContext);
				final TextView rowTextView = new TextView(mContext);
				final ImageView rowImageView = new ImageView(mContext);

				layout.setBackgroundColor(Color.GREEN);
				layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 300));
				rowImageView.setLayoutParams(new LayoutParams(300, 300));
				rowTextView.setPadding(50, 100, 0, 0);

				String title = "Title is empty";
				if (obj.get(Constants.MISSON_TITLE) != null) {
					title = obj.get(Constants.MISSON_TITLE).toString();
				}

				title += "_" + obj.get(Constants.MISSON_MONGO_DB_ID).toString();
				rowImageView.setImageResource(R.drawable.abc_ab_bottom_solid_dark_holo);
				showPhotoFromUrl(rowImageView, Constants.makeThumnailUrl(obj.getString(Constants.MISSON_MONGO_DB_ID)));
				rowTextView.setText(title);
				rowTextView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						startMissionActivity(obj);
					}
				});

			    // add the textview to the linearlayout
				layout.addView(rowImageView);
				layout.addView(rowTextView);
				mainPageLinearLayout.addView(layout);

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
    
    private void showPhotoFromUrl(ImageView imageView, String url) {
    	new DownloadImageTask(imageView).execute(url);
    }
    
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
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
        }
    }
}
