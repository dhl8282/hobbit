package com.example.hobbit;

import org.bson.types.ObjectId;

import android.app.ProgressDialog;
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

import com.example.hobbit.util.AppPrefs;
import com.example.hobbit.util.Constants;
import com.example.hobbit.util.Database;
import com.example.hobbit.util.GPSTracker;
import com.example.hobbit.util.ImageProcess;
import com.example.hobbit.util.Mission;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class CreateMissionActivity extends BaseActivity {

    private static final String TAG = "hobbit" + CreateMissionActivity.class.getSimpleName();
    private GPSTracker gps;
    private ImageView imageViewPicPreview;
    private EditText editTextMissionTitle, editTextHint;
    private Button buttonTouchMe;
    private String missionTitle, hint;
    private double longitude, latitude;
    private GoogleMap map;
    private Bitmap photoBitmap;
    private Mission missionItem;
    private ObjectId missionAndPhotoId;
    private String mPhotoAbsolutePath;
    private Mission parentMission = null;
    private boolean hasParentMission = false;
    private Database mongoDB = null;
    private DBCollection parentCollection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_preview);
        if (getIntent().hasExtra(Constants.INTENT_EXTRA_PARENT_MISSION)) {
            parentMission = (Mission) getIntent().getExtras().get(Constants.INTENT_EXTRA_PARENT_MISSION);
            hasParentMission = true;
        }
        createMission();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(this, MainMenuActivity.class);
//        startActivity(intent);
        finish();
    }

    private void createMission() {
        imageViewPicPreview = (ImageView) findViewById(R.id.imageViewPicPreview);
        editTextMissionTitle = (EditText) findViewById(R.id.editTextMissionTitle);
        editTextHint = (EditText) findViewById(R.id.editTextHint);
        buttonTouchMe = (Button) findViewById(R.id.buttonTouchMe);
        Intent intent = getIntent();
        mPhotoAbsolutePath = (String) intent.getExtras().get(Constants.INTENT_EXTRA_PHOTO_ABS_PATH);
        photoBitmap = ImageProcess.getBitmapFromMemCache(mPhotoAbsolutePath);
        //DiskLruImageCache disk = new DiskLruImageCache(this);
        //photoBitmap = disk.getBitmap(mPhotoAbsolutePath);
        imageViewPicPreview.setImageBitmap(photoBitmap);

        if(parentMission != null) {
            editTextMissionTitle.setText("Re :" + parentMission.getTitle());
        }

        buttonTouchMe.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                missionTitle = editTextMissionTitle.getText().toString();
                hint = editTextHint.getText().toString();
                getGPSLocation();
                AppPrefs appPrefs = new AppPrefs(getApplicationContext());
                missionItem = new Mission(missionTitle, hint, longitude, latitude);
                if (parentMission != null) {
                    missionItem.setParentInfo(parentMission);
                }
                String userId = appPrefs.getUserId();
                missionItem.setUserId(userId);
                missionItem.setLocalPhotoPath(mPhotoAbsolutePath);
                CreateMissionTask createMissionTask = new CreateMissionTask(missionItem);
                createMissionTask.execute();
                if (hasParentMission) {
                    UpdateMissionTask updateMissionTask = new UpdateMissionTask();
                    updateMissionTask.execute(parentMission.getMissionId());
                }
            }
        });
    }

    private void uploadPhotoToAWSS3(Mission mission) {
        Intent intent = new Intent(this, S3UploaderActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_MISSION, mission);
        Log.d(TAG, "S3Uploder is called to upload the photo with unique id from mongodb");
        startActivity(intent);
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
            mark = new LatLng(mission.getLatitude(), mission.getLongitude());
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
        ProgressDialog dialog;

        public CreateMissionTask(Mission mission) {
            mMission = mission;
        }

        protected void onPreExecute() {
            dialog = new ProgressDialog(CreateMissionActivity.this);
            dialog.setMessage(CreateMissionActivity.this
                    .getString(R.string.uploading));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            createMissionToDB(mMission);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            uploadPhotoToAWSS3(mMission);
        }
    }

    private void createMissionToDB(Mission mission) {
        mongoDB = new Database();
        Log.d(TAG, "mongo db connected successfully");
        if (mongoDB != null && mission != null) {
            parentCollection = mongoDB.getCollection(Database.COLLECTION_PARENT_MISSION);
            DBCollection replyMissionCollection;

            BasicDBObject document = new BasicDBObject();
            document.put(Constants.USER_ID, mission.getUserId());
            document.put(Constants.MISSION_TITLE, mission.getTitle());
            document.put(Constants.MISSION_HINT, mission.getHint());
            document.put(Constants.MISSION_COUNT_VIEW, 0);
            document.put(Constants.MISSION_COUNT_TRY, 0);
            document.put(Constants.MISSION_COUNT_SUCCESS, 0);
            document.put(Constants.MISSION_COUNT_FAIL, 0);
            double[] loc = {mission.getLatitude(), mission.getLongitude()};
            document.put(Constants.MISSION_LOC, loc);
            if (hasParentMission) {
                document.put(Constants.MISSION_PARENT_MISSION_ID, parentMission.getMissionId());
                document.put(Constants.MISSION_PARENT_USER_ID, parentMission.getUserId());
                document.put(Constants.MISSION_BOOLEAN_REPLY, Constants.MISSION_BOOLEAN_NONE);
                replyMissionCollection = mongoDB.getCollection(Database.COLLECTION_MISSION_REPLY);
                replyMissionCollection.insert(document);
                missionAndPhotoId = (ObjectId) document.get(Constants.MISSION_MONGO_DB_ID);
                
                BasicDBObject compDocument = new BasicDBObject();
                compDocument.put(Constants.MISSION_PARENT_MISSION_ID, parentMission.getMissionId());
                compDocument.put(Constants.MISSION_MISSION_RESPONSE_ID, missionAndPhotoId);
                compDocument.put(Constants.MISSION_BOOLEAN_REPLY, Constants.MISSION_BOOLEAN_NONE);
            } else {
                parentCollection.insert(document);
            }

            missionAndPhotoId = (ObjectId) document.get(Constants.MISSION_MONGO_DB_ID);
            updateParentMissionToDB(parentCollection);
            
            mission.setMongoDBId(missionAndPhotoId.toString());
            Log.d(TAG, "Mission is created in DB with the id " + missionAndPhotoId.toString());
        }
    }

    private void updateParentMissionToDB(DBCollection collection) {
        if (!hasParentMission) {
            return;
        }
        BasicDBObject newDocument =
                new BasicDBObject().append("$inc",
                new BasicDBObject().append(Constants.MISSION_COUNT_TRY, 1));

        collection.update(new BasicDBObject().append(Constants.MISSION_MONGO_DB_ID, parentMission.getMissionId()), newDocument);
//        collection.find( { Constants.MISSON_MONGO_DB_ID: parentMission.getMissionId()});
    }

    private class UpdateMissionTask extends AsyncTask<String, Void, String> {

        private void updateParentMissionToDB(String missionId) {
            if (!hasParentMission || mongoDB == null || parentCollection == null) {
                return;
            }
            BasicDBObject query = new BasicDBObject();
            query.put(Constants.MISSION_MONGO_DB_ID, new ObjectId(missionId));
            BasicDBObject incValue = new BasicDBObject(Constants.MISSION_COUNT_TRY, Constants.ONE);
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
            Log.d(TAG, "Parent mission try count is updated");
            super.onPostExecute(result);
        }
    }
}
