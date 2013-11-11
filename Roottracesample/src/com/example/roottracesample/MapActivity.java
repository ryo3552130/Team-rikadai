package com.example.roottracesample;



import java.util.Date;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements
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
	public boolean recording = false;
	Button Start_btn,Stop_btn;
	Date nowdate;
	static int flag=0;
	static final String DB = "location.db";
	static final int DB_VERSION = 1;
	static final String DROP_TABLE = "drop table locationdata;";
	public static final String TABLE_NAME = "locationdata";
	public static final String ID = "id";
	public static final String Lat = "lat";
	public static final String Lot = "lot";
	public static final String Date = "date";
	static SQLiteDatabase mydb;
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
		MySQLiteOpenHelper hlpr = new MySQLiteOpenHelper(getApplicationContext());
		mydb = hlpr.getWritableDatabase();

	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
		Start_btn = (Button)findViewById(R.id.Startbtn);
		Stop_btn= (Button)findViewById(R.id.Stopbtn);
		Start_btn.setOnClickListener(this);
		Stop_btn.setOnClickListener(this);
		mMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng point) {
				get_location_text = (TextView) findViewById(R.id.GLtext);
				String text = "取得座標:latitude=" + point.latitude + ", longitude=" + point.longitude;
				targetLat=point.latitude;
				targetLot=point.longitude;
				float[] result = BetweenCul(currentLat,currentLot,targetLat,targetLot);
				destination_text = (TextView) findViewById(R.id.Dtext);
				StringBuilder Stext = new StringBuilder();
				Stext.append("距離(m)" + result[0]);		
				Stext.append("\n角度" + result[1]);	

				destination_text.setText(Stext);
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

	public float[] BetweenCul(double currentLat1 ,double currentLot1, double targetLat1 , double targetLot1) {		
		double startLati =  currentLat1 ;		
		double startLong = currentLot1;		
		double endLati =  targetLat1;		
		double endLong =  targetLot1;		
		float[] result = new float[3];		
		Location.distanceBetween(startLati, startLong, endLati, endLong, result);
		return result;
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

		if(recording){
			nowdate = new Date();
			String now = nowdate.toString();
			String ScurrentLat = ""+currentLat;
			String ScurrentLot = ""+currentLot;
			//InsertDate(ScurrentLat,ScurrentLot,now);
			InsertDate("aaa","ccc","vvvv");
		}
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
		if (v == Start_btn){
			Toast.makeText(this, "Start recording", Toast.LENGTH_LONG).show();
			recording = true;


		}else if (v == Stop_btn){
			Toast.makeText(this, "Stop recording", Toast.LENGTH_LONG).show();
			recording = false;
		}

	}	



	private static class MySQLiteOpenHelper extends SQLiteOpenHelper {
		public MySQLiteOpenHelper(Context c) {
			super(c, DB, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO 自動生成されたメソッド・スタブ
			db.execSQL("CREATE TABLE IF NOT EXISTS '"+TABLE_NAME+"' (" +
					"'"+ ID + "' INTEGER PRIMARY KEY AUTOINCREMENT," +
					"'"+ Lat + "' TEXT NOT NULL,"+
					"'"+ Lot + "' TEXT NOT NULL,"+
					"'"+ Date + "' TEXT NOT NULL"+
					");");



		}


		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(DROP_TABLE);
			onCreate(db);
		}
	}
	public void InsertDate(String lat,String lot,String date){
		//if there is same name,call update.
		ContentValues insertValues = new ContentValues();
		insertValues.put(Lat,lat);
		insertValues.put(Lot,lot);
		insertValues.put(Date,date);
		long ret = mydb.insert(TABLE_NAME, null, insertValues);
		if (ret == -1) {//if insert is failed,it returns -1.
            Toast.makeText(this, "Insert失敗", Toast.LENGTH_SHORT).show();  
        } else {   
            Toast.makeText(this, "Insert成功", Toast.LENGTH_SHORT).show();  
        }  
	}

	private static  void SearchDate(){
		// This time,I use rawQuery.
		String SQL_SELECT = "SELECT * FROM "+ DB + ";";
		//selectionArgs : If you want to use where phrase　,this sentence will be used.
	}


}

