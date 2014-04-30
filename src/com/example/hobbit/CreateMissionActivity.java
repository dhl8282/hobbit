package com.example.hobbit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CreateMissionActivity extends Activity {

    private GPSTracker gps;
    private ImageView imageViewPicPreview;
    private EditText editTextMissionTitle, editTextHint;
    private Button buttonTouchMe;
    private String missionTitle, hint;
    private double longitude, latitude;
    private LatLng mark;
    private GoogleMap map;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_preview);
        showPic();
    }

    private void showPic() {
        imageViewPicPreview = (ImageView) findViewById(R.id.imageViewPicPreview);
        editTextMissionTitle = (EditText) findViewById(R.id.editTextMissionTitle);
        editTextHint = (EditText) findViewById(R.id.editTextHint);
        buttonTouchMe = (Button) findViewById(R.id.buttonTouchMe);
        Intent intent = getIntent();
        imageBitmap = (Bitmap) intent.getExtras().get("picture");
        imageViewPicPreview.setImageBitmap(imageBitmap);

        buttonTouchMe.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                missionTitle = editTextMissionTitle.getText().toString();
                hint = editTextHint.getText().toString();
                setContentView(R.layout.map_fragment);
                getGPSLocation();
                mark = new LatLng(latitude, longitude);
                showPicInGoogleMap(imageBitmap);
            }
        });
    }

    private void getGPSLocation() {
        gps = new GPSTracker(this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Log.d("hobbitCreateMission", "lat is " + latitude);
            Log.d("hobbitCreateMission", "long is " + longitude);
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    private void showPicInGoogleMap(Bitmap imageBitmap) {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        if (map!=null){
          Marker markMarker = map.addMarker(new MarkerOptions()
              .position(mark)
              .title(missionTitle)
              .snippet(hint)
              .icon(BitmapDescriptorFactory
                      .fromBitmap(imageBitmap)));
        }

      //Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mark, 18));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
    }
}
