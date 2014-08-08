package com.example.pics;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bson.types.ObjectId;

import com.example.hobbit.R;
import com.example.pics.util.Constants;
import com.example.pics.util.Database;
import com.example.pics.util.Mission;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ValidateActivity extends Activity {

    private static final String TAG = "hobbit" + ValidateActivity.class.getSimpleName();
    private final String FAIL = "fail";
    private final String SUCCESS = "success";
    private ImageView image1, image2, correctButton, incorrectButton;
    private TextView skipButton;
    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    public TextView timer;
    private final long startTime = 10 * 1000;
    private final long interval = 1 * 1000;
    private Database mongoDB = null;
    private DBCollection replyCollection = null;
    private ArrayList<String> parentList;
    private ArrayList<String> replyList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        image1 = (ImageView) findViewById(R.id.imageview_validate_above);
        image2 = (ImageView) findViewById(R.id.imageview_validate_below);
        correctButton = (ImageView) findViewById(R.id.button_validate_correct);
        incorrectButton = (ImageView) findViewById(R.id.button_validate_incorrect);
        skipButton = (TextView) findViewById(R.id.button_validate_skip);
        timer = (TextView) findViewById(R.id.textview_validate_timer);
        timer.setText(String.valueOf(startTime / 1000));
        if (getIntent().hasExtra(Constants.MISSION_VALIDATION_PARENT_LIST)) {
            Bundle bundle = getIntent().getExtras();
            parentList = bundle.getStringArrayList(Constants.MISSION_VALIDATION_PARENT_LIST);
            replyList = bundle.getStringArrayList(Constants.MISSION_VALIDATION_REPLY_LIST);
        }
        if (replyList.size() > 1) {
            String replyId = replyList.remove(replyList.size()-1);
            String parentId = parentList.remove(parentList.size()-1);
            startValidation(replyId, parentId);
        } else {
            new AlertDialog.Builder(ValidateActivity.this)
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

    private void startValidation(String rId, String pId) {
        //set image
        String rpyUrl = Constants.makeOriginalUrl(rId);
        String oriUrl = Constants.makeOriginalUrl(pId);
        if (Math.random()%2 == 1) {
            new DownloadImageTask(image1).execute(rpyUrl);
            new DownloadImageTask(image2).execute(oriUrl);
        } else {
            new DownloadImageTask(image1).execute(oriUrl);
            new DownloadImageTask(image2).execute(rpyUrl);
        }
        
        //start time
        startCounter();
        
        //add buttons
        addCorrectButton(rId);
        addIncorrectButton(rId);
    }
    
    private void addCorrectButton(final String missionId) {
        correctButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                new UpdateMissionTask().execute(missionId, SUCCESS);
            }
        });
    }
    
    private void addIncorrectButton(final String missionId) {
        incorrectButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                new UpdateMissionTask().execute(missionId, FAIL);
            }
        });
    }
    
    private void addSkipButton() {
        
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ProgressDialog dialog;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected void onPreExecute() {
            dialog = new ProgressDialog(ValidateActivity.this);
            dialog.setMessage(ValidateActivity.this
                    .getString(R.string.loading));
            dialog.setCancelable(false);
            dialog.show();
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

        protected void onPostExecute(final Bitmap result) {
            if (result != null) {
//                Bitmap scaledBitmap = Bitmap.createScaledBitmap(result, 200, 300, true);
//                if (scaledBitmap != null) {
//                    bmImage.setImageBitmap(scaledBitmap);
                    bmImage.setImageBitmap(result);
//                }
                    bmImage.setOnClickListener(new OnClickListener() {
                        
                        @Override
                        public void onClick(View arg0) {
                            final Intent intent = new Intent(getApplicationContext(), ZoomImageActivity.class)
                                    .putExtra(Constants.INTENT_EXTRA_PHOTO_BITMAP, result);
                            startActivity(intent);
                        }
                    });
            }
            dialog.dismiss();
        }
    }
    
    private class UpdateMissionTask extends AsyncTask<String, Void, String> {

        private void updateReplyMission(String missionId, String flag) {
            if (replyCollection == null) {
                return;
            }
            String key = Constants.MISSION_COUNT_SUCCESS;
            if (flag != null && flag.equalsIgnoreCase(FAIL)) {
                key = Constants.MISSION_COUNT_FAIL;
            }
            BasicDBObject query = new BasicDBObject();
            query.put(Constants.MISSION_MONGO_DB_ID, new ObjectId(missionId));
            BasicDBObject incValue = new BasicDBObject(key, Constants.ONE);
            BasicDBObject intModifier = new BasicDBObject(Database.MONGODB_INCREMENT, incValue);
            replyCollection.update(query, intModifier);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "Mission to be updated is " + params[0]);
            updateReplyMission(params[0], params[1]);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "Reply mission view count is updated");
            super.onPostExecute(result);
            
            new AlertDialog.Builder(ValidateActivity.this)
                    .setTitle("Notification")
                    .setMessage("Thank you for validation!")
                    .setPositiveButton("End", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) { 
                                finish();
                            }
                    })
                    .setNegativeButton("Next", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), ValidateActivity.class);
                                intent.putStringArrayListExtra(Constants.MISSION_VALIDATION_PARENT_LIST, parentList);
                                intent.putStringArrayListExtra(Constants.MISSION_VALIDATION_REPLY_LIST, replyList);
                                startActivity(intent);
                                finish();
                            }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}