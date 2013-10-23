package com.example.roottracesample;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

	public class MainActivity extends FragmentActivity implements
	ConnectionCallbacks,
	OnConnectionFailedListener,
	LocationListener,
	OnMyLocationButtonClickListener,
	OnClickListener
	{

		private GoogleMap mMap;
		double currentLat=0,currentLot=0,targetLat=0,targetLot=0;
		private LocationClient mLocationClient;
		private TextView my_location_text;
		private TextView destination_text;
		private TextView get_location_text;
		Button my_location_btn;
		Button  destination_btn;
		int flag=0;
		// These settings are the same as the settings for the map. They will in fact give you updates
		// at the maximal rates currently possible.
		private static final LocationRequest REQUEST = LocationRequest.create()
				.setInterval(5000)         // 5 seconds
				.setFastestInterval(16)    // 16ms = 60fps
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_map_main);
my_location_text = (TextView) findViewById(R.id.MLtext);

}

@Override
protected void onResume() {
super.onResume();
setUpMapIfNeeded();
setUpLocationClientIfNeeded();
mLocationClient.connect();
my_location_btn = (Button)findViewById(R.id.MLbtn);
destination_btn= (Button)findViewById(R.id.Dbtn);
my_location_btn.setOnClickListener(this);
destination_btn.setOnClickListener(this);
mMap.setOnMapClickListener(new OnMapClickListener() {
    @Override
    public void onMapClick(LatLng point) {
    	get_location_text = (TextView) findViewById(R.id.GLtext);
    	String text = "取得座標:latitude=" + point.latitude + ", longitude=" + point.longitude;
    	get_location_text.setText(text);	
    }
});

mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
    @Override
    public void onMapLongClick(LatLng point) {
    	
    	//Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("geo:0,0?q=Osaka"));
    	
    	destination_text = (TextView) findViewById(R.id.Dtext);
			String text = "目的地:latitude=" + point.latitude + ", longitude=" + point.longitude;
			destination_text.setText(text);	
			
			targetLat = point.latitude;
			targetLot = point.longitude;
			
			//String targeturl = "&daddr=" + targetLat + "," + targetLot;
			String url = "http://maps.google.com/maps?";
			  url += "myl=saddr"; 
			  url += "&daddr=" + targetLat + "," + targetLot;
			  url += "&dirflg=w";
			
			  Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
			intent.setData(Uri.parse(url));
			startActivity(intent);
    	
    }
});
}

@Override
public void onPause() {
super.onPause();
if (mLocationClient != null) {
    mLocationClient.disconnect();
}
}

private void setUpMapIfNeeded() {
// Do a null check to confirm that we have not already instantiated the map.
if (mMap == null) {
    // Try to obtain the map from the SupportMapFragment.
    mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
            .getMap();
    // Check if we were successful in obtaining the map.
    if (mMap != null) {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
    }
}
}

private void setUpLocationClientIfNeeded() {
if (mLocationClient == null) {
    mLocationClient = new LocationClient(
            getApplicationContext(),
            this,  // ConnectionCallbacks
            this); // OnConnectionFailedListener
}
}

/**
* Button to get current Location. This demonstrates how to get the current Location as required
* without needing to register a LocationListener.
*/
public void showMyLocation(View view) {
if (mLocationClient != null && mLocationClient.isConnected()) {
    String msg = "Location = " + mLocationClient.getLastLocation();
    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
}
}

/**
* Implementation of {@link LocationListener}.
*/
@Override
public void onLocationChanged(Location loc) {
	//my_location_text.setText("Location = " + loc);
	LatLng curr = new LatLng(loc.getLatitude(), loc.getLongitude());
	   LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
	   currentLat = latLng.latitude;
	   currentLot = latLng.longitude;
	   my_location_text.setText("Latitude"+loc.getLatitude()+"Longitude"+loc.getLongitude());
}

/**
* Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
*/
@Override
public void onConnected(Bundle connectionHint) {
mLocationClient.requestLocationUpdates(
        REQUEST,
        this);  // LocationListener
}

/**
* Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
*/
@Override
public void onDisconnected() {
// Do nothing
}

/**
* Implementation of {@link OnConnectionFailedListener}.
*/
@Override
public void onConnectionFailed(ConnectionResult result) {
// Do nothing
}

@Override
	public boolean onMyLocationButtonClick() {
	Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
	// Return false so that we don't consume the event and the default behavior still occurs
	// (the camera animates to the user's current position).
	return false;
	}

@Override
public void onClick(View v) {
	// TODO 自動生成されたメソッド・スタブ
	if (v == my_location_btn){
		Toast.makeText(this, "MyLocation button was clicked", Toast.LENGTH_LONG).show();
		
		
        
    }else if (v == destination_btn){
    	Toast.makeText(this, "destination　button was clicked", Toast.LENGTH_LONG).show();
	
    }
	
	
	
	
	
}



	
	
	
	
}
















