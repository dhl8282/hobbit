package com.example.hobbit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hobbit.util.Constants;
import com.example.hobbit.util.Mission;

public class MissionActivity extends Activity {

	private static final String TAG = "hobbit" + MissionActivity.class.getSimpleName();
    private TextView missionTitle, hintContent;
    private ImageView missionImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        missionTitle = (TextView) findViewById(R.id.textViewMissionTitle);
        hintContent = (TextView) findViewById(R.id.textViewHintContent);
        missionImage = (ImageView) findViewById(R.id.imageViewMissionImage);
        Mission mission = (Mission) getIntent().getExtras().get(Constants.INTENT_EXTRA_MISSION);
        try {
			showMission(mission);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void showMission(Mission mission) throws IOException {
    	if (mission != null) {
	    	missionTitle.setText(mission.getTitle());
	    	hintContent.setText(mission.getHint());
	    	// TODO : Differentiate show pic from DB to Local
//	    	new DownloadImageTask(missionImage).execute(mission.getPhotoUrl());
//	    	Log.d(TAG, "Local file path is " + mission.getLocalPhotoPath());
//	    	File imgFile = new  File(mission.getLocalPhotoPath());
//	    	if(imgFile.exists()){
//
//	    	    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//	    	    Bitmap scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 200, 300, true);
//	    	    missionImage.setImageBitmap(scaledBitmap);
//
//	    	}
    	}
    }
    
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ProgressDialog dialog;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected void onPreExecute() {
            dialog = new ProgressDialog(MissionActivity.this);
            dialog.setMessage(MissionActivity.this
                    .getString(R.string.loading));
            dialog.setCancelable(false);
            dialog.show();
        }
        
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            dialog.dismiss();
        }
    }
}
