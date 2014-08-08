package com.example.pics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.hobbit.R;
import com.example.hobbit.R.layout;
import com.example.pics.util.Constants;
import com.example.pics.util.Database;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PrepareValidationActivity extends Activity {

    private Database mongoDB = null;
    private DBCollection replyCollection = null;
    private static final String TAG = "hobbit" + PrepareValidationActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetValidationListTask().execute();
    }
    
    private class GetValidationListTask extends AsyncTask<String, Void, List<BasicDBObject>> {
        ProgressDialog dialog;
        
        private List<BasicDBObject> getValidationList() {
            mongoDB = new Database();
            List<BasicDBObject> missionsToValidate = new ArrayList<BasicDBObject>();
            if (mongoDB != null) {
                Log.d(TAG, "mongo db connected successfully");
                replyCollection = mongoDB.getCollection(Database.COLLECTION_MISSION_REPLY);
                BasicDBObject query = new BasicDBObject();
                query.put(Constants.USER_SUCCESS, new BasicDBObject("$lt", Constants.DEFAULT_VALIDATION_COUNT));
                query.put(Constants.USER_FAIL, new BasicDBObject("$lt", Constants.DEFAULT_VALIDATION_COUNT));
                DBCursor cursor = replyCollection.find(query);
                while (cursor.hasNext()) {
                    BasicDBObject obj = (BasicDBObject) cursor.next();
                    missionsToValidate.add(obj);
                }
            }
            return missionsToValidate;
        }
        
        protected void onPreExecute() {
            dialog = new ProgressDialog(PrepareValidationActivity.this);
            dialog.setMessage(PrepareValidationActivity.this
                    .getString(R.string.loading));
            dialog.setCancelable(false);
            dialog.show();
        }
        
        @Override
        protected List<BasicDBObject> doInBackground(String... params) {
            return getValidationList();
        }

        @Override
        protected void onPostExecute(List<BasicDBObject> missionsToValidate) {
            Collections.shuffle(missionsToValidate);
            Collections.shuffle(missionsToValidate);
            ArrayList<String> parentList = new ArrayList<String>();
            ArrayList<String> replyList = new ArrayList<String>();
            
            for (BasicDBObject obj : missionsToValidate) {
                parentList.add(obj.get(Constants.MISSION_PARENT_MISSION_ID).toString());
                replyList.add(obj.get(Constants.MISSION_MONGO_DB_ID).toString());
            }
            dialog.dismiss();
            startValidationActivity(replyList, parentList);
        }
        
        private void startValidationActivity(ArrayList<String> rList, ArrayList<String> pList) {
            if (rList.size() > 1) {
                Intent intent = new Intent(getApplicationContext(), ValidateActivity.class);
                intent.putStringArrayListExtra(Constants.MISSION_VALIDATION_PARENT_LIST, pList);
                intent.putStringArrayListExtra(Constants.MISSION_VALIDATION_REPLY_LIST, rList);
                startActivity(intent);
                finish();
            } else { // if the validation list is empty
                new AlertDialog.Builder(PrepareValidationActivity.this)
                        .setTitle("Notification")
                        .setMessage("Validation list is empty!")
                        .setPositiveButton("End", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) { 
                                    finish();
                                }
                        })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
            }
        }
    }
}
