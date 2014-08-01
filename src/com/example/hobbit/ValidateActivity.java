package com.example.hobbit;

import java.util.ArrayList;
import java.util.List;

import com.example.hobbit.util.Constants;
import com.example.hobbit.util.Database;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class ValidateActivity extends Activity {

    private static final String TAG = "hobbit" + ValidateActivity.class.getSimpleName();
    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    public TextView timer;
    private final long startTime = 10 * 1000;
    private final long interval = 1 * 1000;
    private Database mongoDB = null;
    private DBCollection replyCollection = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        timer = (TextView) findViewById(R.id.textview_validate_timer);
        timer.setText(String.valueOf(startTime / 1000));
        startCounter();
        GetValidationListTask task = new GetValidationListTask();
        task.execute();
    }

    private class GetValidationListTask extends AsyncTask<String, Void, List<BasicDBObject>> {
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
                //Log.d("TAG", "Total validate cantidates areeeee " + cursor.length());
                while (cursor.hasNext()) {
                    BasicDBObject obj = (BasicDBObject) cursor.next();
                    missionsToValidate.add(obj);
                }
            }
            return missionsToValidate;
        }
        
        @Override
        protected List<BasicDBObject> doInBackground(String... params) {
            return getValidationList();
        }

        @Override
        protected void onPostExecute(List<BasicDBObject> missionsToValidate) {
            
        }
    }
    
    private void startCounter(){
        countDownTimer = new MyCountDownTimer(startTime, interval);
        if (!timerHasStarted) {
            countDownTimer.start();
            timerHasStarted = true;
        } else {
            countDownTimer.cancel();
            timerHasStarted = false;
        }
    }
    
    public class MyCountDownTimer extends CountDownTimer{
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            timer.setText("END");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timer.setText("" + millisUntilFinished / 1000);
        }
    }
}
