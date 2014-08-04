package com.example.hobbit;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class EnterMissionActivity extends FragmentActivity {

    private static final String TAG = "hobbit" + EnterMissionActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
