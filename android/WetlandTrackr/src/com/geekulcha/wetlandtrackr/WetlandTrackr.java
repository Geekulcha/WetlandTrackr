package com.geekulcha.wetlandtrackr;

import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.widget.Toast;

public class WetlandTrackr extends Activity {

	LocationManager locMan;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wetland_trackr);
		
		//Let's test if the internet connection is available
		if(isNetworkAvailable()) {
			
			//get location manager
			locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			//get last location
			Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			double lat = lastLoc.getLatitude();
			double lng = lastLoc.getLongitude();
			
			Toaster(String.valueOf(lat) + " \n " + String.valueOf(lng));
		}
		else {
			Toaster("You don't seem connected to a network");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wetland_trackr, menu);
		return true;
	}
	
	//Method to check network availbility
	private boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    
      NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnected();
}
	
//If the user want to exit from the app
	public void Exit()
	{
		new AlertDialog.Builder(this)
        .setMessage("Are you sure you want to exit?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                 WetlandTrackr.this.finish();
            }
        })
        .setNegativeButton("No", null)
        .show();
	}
	
	//Toaster nmethod 
	
   public void Toaster(String bread) {
	   Toast.makeText(WetlandTrackr.this, bread, Toast.LENGTH_SHORT).show();
	   
   }

}
