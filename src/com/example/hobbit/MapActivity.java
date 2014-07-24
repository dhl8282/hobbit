package com.example.hobbit;

import com.example.hobbit.util.Constants;
import com.example.hobbit.util.GPSTracker;
import com.example.hobbit.util.Mission;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Mission mission = (Mission) intent.getParcelableExtra(Constants.INTENT_EXTRA_MISSION);
		showInTheMap(mission);
	}
	
	private void showInTheMap(Mission mission) {
        setContentView(R.layout.map_fragment);
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        LatLng currentLocation = getCurrentLocation();

        if (map != null){
            Marker markMarker = map.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("You"));
        }

        if (currentLocation != null) {
            //Move the camera instantly to mark with a zoom of 12.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
        }
        makeMissionMarkersInMap(map, mission);
    }
    
    private void makeMissionMarkersInMap(GoogleMap map, Mission mission) {
    	LatLng mark = new LatLng(mission.getLatitude(), mission.getLongitude());
        map.addMarker(new MarkerOptions()
                .position(mark)
                .title(mission.getTitle())
                .snippet(mission.getHint()));
    
    }
    
    private LatLng getCurrentLocation() {
    	GPSTracker gps = new GPSTracker(this);
        double latitude = 0;
        double longitude = 0;

        // check if GPS enabled
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            gps.showSettingsAlert();
        }

        return new LatLng(latitude, longitude);
    }
}
