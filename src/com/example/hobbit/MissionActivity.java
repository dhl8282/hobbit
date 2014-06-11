package com.example.hobbit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hobbit.util.Constants;
import com.example.hobbit.util.ImageProcess;
import com.example.hobbit.util.Mission;

public class MissionActivity extends Activity {

	private static final String TAG = "hobbit" + MissionActivity.class.getSimpleName();
    private TextView missionTitle, hintContent;
    private ImageView missionImage;
    private Button startMissionButton;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }
    
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
        
        addStartMissionButton(mission);
    }

    private void addStartMissionButton(final Mission mission) {
    	startMissionButton = (Button) findViewById(R.id.buttonStart);
    	startMissionButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final Intent intent = new Intent(getApplicationContext(), PrepareCreateMissionActivity.class);
				intent.putExtra(Constants.INTENT_EXTRA_MISSION, mission);
				startActivity(intent);
			}
		});
    }
    private void showMission(Mission mission) throws IOException {
    	if (mission != null) {
	    	missionTitle.setText(mission.getTitle());
	    	hintContent.setText(mission.getHint());
	    	
	    	if (!mission.getLocalPhotoPath().isEmpty()) {
	    		showPhotoFromLocal(mission.getLocalPhotoPath());
	    	} else {
	    		showPhotoFromUrl(mission.getPhotoUrl());
	    	}
    	}
    }
    
    private void showPhotoFromLocal(String localPath) {
    	File imgFile = new  File(localPath);
    	if(imgFile.exists()){

    	    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//    	    Bitmap scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 200, 300, true);
    	    Bitmap scaledBitmap = ImageProcess.getScaledImageFromBitmap(myBitmap, localPath);
    	    missionImage.setImageBitmap(scaledBitmap);
    	}
    }
    
    private void showPhotoFromUrl(String url) {
    	new DownloadImageTask(missionImage).execute(url);
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
            dialog.dismiss();
        }
    }
}
