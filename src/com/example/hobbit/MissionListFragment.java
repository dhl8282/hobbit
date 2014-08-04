package com.example.hobbit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.example.hobbit.R;
import com.example.hobbit.util.Constants;
import com.example.hobbit.util.Database;
import com.example.hobbit.util.GPSTracker;
import com.example.hobbit.util.Mission;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class MissionListFragment extends ListFragment {

    private static final String TAG = "hobbit" + MissionListFragment.class.getSimpleName();
    private Database mongoDB = null;
    private DBCollection parentCollection = null;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        LatLng currentLoc = getCurrentLocation();
        GetMissionsTask task = new GetMissionsTask(getActivity(), currentLoc);
        task.execute();
        return v;
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
      super.onListItemClick(l, v, pos, id);
      Mission mission = (Mission) l.getItemAtPosition(pos);
//      Toast.makeText(getActivity(), "Item " + pos + " was clicked" + a, Toast.LENGTH_SHORT).show();
      Intent intent = new Intent(getActivity(), MissionActivity.class);
      intent.putExtra(Constants.INTENT_EXTRA_MISSION, mission);
      startActivity(intent);
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
                makeMissionList(missions);
            }
        }

        private void makeMissionList(List<BasicDBObject> missions) {
            if (missions.size() == 0) {
                Log.d(TAG, "No mission returned");
                return;
            }

            ArrayList<Mission> items = new ArrayList<Mission>();
            
            for (final BasicDBObject obj : missions) {
                items.add(makeMissionFromDB(obj));
            }

            setListAdapter(new MissionAdapter(getActivity(), items));
        }

        private Mission makeMissionFromDB(BasicDBObject obj) {
            String title = "";
            String hint = "";
            String userId = "";
            String missionId = "";
            Double lng, lat;
            if (obj.get(Constants.MISSION_TITLE) != null) {
                title = obj.get(Constants.MISSION_TITLE).toString();
            }
            if (obj.get(Constants.MISSION_HINT) != null) {
                hint = obj.get(Constants.MISSION_HINT).toString();
            }
            if (obj.get(Constants.USER_ID) != null) {
                userId = obj.get(Constants.USER_ID).toString();
            }

            BasicDBList loc = (BasicDBList)obj.get(Constants.MISSION_LOC);
            lng = (Double) loc.get(0);
            lat = (Double) loc.get(1);

            missionId = obj.get(Constants.MISSION_MONGO_DB_ID).toString();
            UpdateMissionTask task = new UpdateMissionTask();
            task.execute(missionId);

            Mission mission = new Mission(title, hint, lng, lat);
            mission.setMissionId(missionId);
            mission.setPhotoUrl(Constants.makeOriginalUrl(missionId));
            mission.setThumnailUrl(Constants.makeThumnailUrl(missionId));
            mission.setUserId(userId);

            return mission;
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
        
        private void makeMissionMarkersInMap(GoogleMap map, List<BasicDBObject> missions) {
            Log.d(TAG, "total mission is " + missions.size());
            for (BasicDBObject obj : missions) {
                BasicDBList loc = (BasicDBList) obj.get(Constants.MISSION_LOC);
                String lat = loc.get(0).toString();
                String lng = loc.get(1).toString();
                LatLng mark = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                map.addMarker(new MarkerOptions()
                        .position(mark)
                        .title(obj.get(Constants.MISSION_TITLE).toString())
                        .snippet(obj.get(Constants.MISSION_HINT).toString()));
            }
        }
    }
    
    private LatLng getCurrentLocation() {
        GPSTracker gps = new GPSTracker(getActivity());
        double latitude = 0;
        double longitude = 0;

        // check if GPS enabled
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Toast.makeText(getActivity(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            gps.showSettingsAlert();
        }

        return new LatLng(latitude, longitude);
    }
    
    private class UpdateMissionTask extends AsyncTask<String, Void, String> {

        private void updateParentMissionView(String missionId) {
            if (parentCollection == null) {
                return;
            }
            BasicDBObject query = new BasicDBObject();
            query.put(Constants.MISSION_MONGO_DB_ID, new ObjectId(missionId));
            BasicDBObject incValue = new BasicDBObject(Constants.MISSION_COUNT_VIEW, Constants.ONE);
            BasicDBObject intModifier = new BasicDBObject(Database.MONGODB_INCREMENT, incValue);
            parentCollection.update(query, intModifier);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "Mission to be updated is " + params[0]);
            updateParentMissionView(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "Parent mission view count is updated");
            super.onPostExecute(result);
        }
    }
}
