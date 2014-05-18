package com.example.hobbit;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.hobbit.util.Database;
import com.example.hobbit.util.GPSTracker;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showMap();
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

        GetMissionsTask task = new GetMissionsTask(map, currentLocation);
        task.execute();
    }

    private List<BasicDBObject> showMissions(GoogleMap map, LatLng currentLoc) {
        Database mongoDB = new Database();
        List<BasicDBObject> missions = new ArrayList<BasicDBObject>();
        if (mongoDB != null) {
            DBCollection collection = mongoDB.getCollection(Database.COLLECTION_PARENT_MISSION);
            Log.d(TAG, "mongo db collection connected successfully");
            double[] loc = {currentLoc.latitude , currentLoc.longitude};
            // TODO : Implement mission show limit
            // Limit to 10 results
            DBCursor cursor = collection.find( new BasicDBObject( "loc", new BasicDBObject("$near", loc))).limit(10);
            while (cursor.hasNext()) {
                BasicDBObject obj = (BasicDBObject) cursor.next();
                missions.add(obj);
            }
        }
        return missions;
    }

    private class GetMissionsTask extends AsyncTask<String, Void, List<BasicDBObject>> {
        GoogleMap mMap;
        LatLng mCurrentLoc;
        public GetMissionsTask(GoogleMap map, LatLng currentLoc) {
            mMap = map;
            mCurrentLoc = currentLoc;
        }

        @Override
        protected List<BasicDBObject> doInBackground(String... params) {
            return showMissions(mMap, mCurrentLoc);
        }

        @Override
        protected void onPostExecute(List<BasicDBObject> missions) {
            if (missions == null) {
                Log.d(TAG, "No missions are found");
            }
            else {
                Log.d(TAG, "total mission is " + missions.size());
                for (BasicDBObject obj : missions) {
                    BasicDBList loc = (BasicDBList) obj.get("loc");
                    String lat = loc.get(0).toString();
                    String lng = loc.get(1).toString();
                    LatLng mark = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    mMap.addMarker(new MarkerOptions()
                            .position(mark)
                            .title(obj.get("title").toString())
                            .snippet(obj.get("hint").toString()));
                }
            }
        }
    }
}
