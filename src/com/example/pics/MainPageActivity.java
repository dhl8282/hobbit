package com.example.pics;

import com.example.hobbit.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainPageActivity extends FragmentActivity {

    private static final String TAG = "hobbit" + MainPageActivity.class.getSimpleName();
    private ImageView mainMenu, mainValidate, mainCreate;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addButtons();
    }
    
    private void addButtons() {
        addMainMenu();
        addMainCreate();
        addMainValidate();
    }
    
    private void addMainMenu() {
        mainMenu = (ImageView) findViewById(R.id.button_main_menu);
    }
    
    private void addMainValidate() {
        mainValidate = (ImageView) findViewById(R.id.button_main_validate);
        mainValidate.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                final Intent intent = new Intent(getApplicationContext(), PrepareValidationActivity.class);
                startActivity(intent);
            }
        });
    }
    
    private void addMainCreate(){
        mainCreate = (ImageView) findViewById(R.id.button_main_create);
        mainCreate.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                final Intent intent = new Intent(getApplicationContext(), PrepareCreateMissionActivity.class);
                startActivity(intent);
            }
        });
    }
    
    
    
//    private LatLng getCurrentLocation() {
//        GPSTracker gps = new GPSTracker(this);
//        double latitude = 0;
//        double longitude = 0;
//
//        // check if GPS enabled
//        if(gps.canGetLocation()){
//            latitude = gps.getLatitude();
//            longitude = gps.getLongitude();
//            Toast.makeText(this, "Your Location is - \nLat: "
//                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
//        }else{
//            gps.showSettingsAlert();
//        }
//
//        return new LatLng(latitude, longitude);
//    }
//    
//    private void showMap() {
//        setContentView(R.layout.map_fragment);
//        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
//                .getMap();
//
//        LatLng currentLocation = getCurrentLocation();
//
//        if (map != null){
//            Marker markMarker = map.addMarker(new MarkerOptions()
//                .position(currentLocation)
//                .title("You"));
//        }
//
//        if (currentLocation != null) {
//            //Move the camera instantly to mark with a zoom of 12.
//            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
//            // Zoom in, animating the camera.
//            map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
//        }
//
//        GetMissionsTask task = new GetMissionsTask(map, currentLocation);
//        task.execute();
//    }

}
