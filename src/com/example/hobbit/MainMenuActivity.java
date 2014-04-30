package com.example.hobbit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button buttonCreateMission, buttonEnterMission;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		addCreateMission();
		addEnterMission();
	}

//	@Override
//	public void onBackPressed() {
//	    // TODO Auto-generated method stub
//	    super.onBackPressed();
//	    Intent intent = new Intent(this, MainMenuActivity.class);
//        startActivity(intent);
//	}

	private void addCreateMission() {
		buttonCreateMission = (Button) findViewById(R.id.buttonCreateMission);
		buttonCreateMission.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			    dispatchTakePictureIntent();
			}
		});
	}

	private void addEnterMission() {
	    buttonEnterMission = (Button) findViewById(R.id.buttonEnterMission);
	    buttonEnterMission.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showMap();
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
	        Intent intent = new Intent(this, CreateMissionActivity.class);
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        intent.putExtra("picture", imageBitmap);
	        startActivity(intent);
	    }
	}

	private void showMap() {
	    setContentView(R.layout.map_fragment);;
	}

}
