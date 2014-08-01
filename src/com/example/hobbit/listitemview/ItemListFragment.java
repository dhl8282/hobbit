package com.example.hobbit.listitemview;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.hobbit.EnterMissionActivity;
import com.example.hobbit.R;
import com.example.hobbit.util.Constants;
import com.example.hobbit.util.Database;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class ItemListFragment extends ListFragment {

    private static final String TAG = "hobbit" + ItemListFragment.class.getSimpleName();
    private Database mongoDB = null;
    private DBCollection parentCollection = null;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        ArrayList<Item> items = new ArrayList<Item>();
        for (int i = 0; i < 100; i++) {
            String url = String.format("http://www.google.com/image/%d.png", i);
            String title = String.format("Item %d", i);
            String description = String.format("Description of Item %d", i);
            Item item = new Item(url, title, description);
            items.add(item);
        }

        setListAdapter(new ItemAdapter(getActivity(), items));

        return v;
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

//        protected void onPreExecute() {
//            dialog = new ProgressDialog(ItemListFragment.this);
//            dialog.setMessage(EnterMissionActivity.this
//                    .getString(R.string.uploading));
//            dialog.setCancelable(false);
//            dialog.show();
//        }

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
//            dialog.dismiss();
            }
        }

        private void makeMissionList(List<BasicDBObject> missions) {
            if (missions.size() == 0) {
                Log.d(TAG, "No mission returned");
                return;
            }

            for (final BasicDBObject obj : missions) {
                String title = "Title is empty";
                if (obj.get(Constants.MISSION_TITLE) != null) {
                    title = obj.get(Constants.MISSION_TITLE).toString();
                }

                title += "_" + obj.get(Constants.MISSION_MONGO_DB_ID).toString();
            }

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
        
    }
}
