package com.example.hobbit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainMenuActivity extends Activity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
	private Button buttonCreateMission;
	private ImageView mImageView;
	private GPSTracker gps;
	private double longitude, latitude;
	private GoogleMap map;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		addCreateMission();
	}

	private void addCreateMission() {
		buttonCreateMission = (Button) findViewById(R.id.buttonCreateMission);
		buttonCreateMission.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			    dispatchTakePictureIntent();
			}
		});
	}

	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        changeLayout();
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        mImageView.setImageBitmap(imageBitmap);
	        getGPSLocation();
	        showPicInGoogleMap();
	    }
	}
	private void changeLayout() {
	    setContentView(R.layout.activity_picture_preview);
	    mImageView = (ImageView) findViewById(R.id.imageViewPicPreview);
	}

	private void getGPSLocation() {
	    gps = new GPSTracker(this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Log.d("test", "lat is " + latitude);
            Log.d("test", "long is " + longitude);
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
	}

	private void showPicInGoogleMap() {

	}
}
