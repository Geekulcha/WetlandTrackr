package com.geekulcha.wetlandtrackr;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LandAccess extends Activity {

    private GoogleMap googleMap;
    int x = 0;
    private double latitude;
    private double longitude;
    
    Button buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_access);
        
        addListenerCancel(); //calling the cancel method

        ActionBar ab = getActionBar();
        ab.hide();

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Unable to show this place", Toast.LENGTH_SHORT)
                        .show();
            }

            Intent i = getIntent();
            latitude = Double.valueOf(i.getStringExtra("latitude").toString());
            longitude = Double
                    .valueOf(i.getStringExtra("longitude").toString());

            // create marker
            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("This is where you are")
                    .snippet(
                            "Latitude: " + String.valueOf(latitude)
                                    + "\n Longitude: "
                                    + String.valueOf(longitude));

            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

            // Move the camera instantly to location with a zoom of 15.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    latitude, longitude), 25));

            // marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            // adding marker
            googleMap.addMarker(marker);

            // up to this point is displaying the users current location

            googleMap.setOnMapClickListener(new OnMapClickListener() {
                
                @Override
                public void onMapClick(LatLng arg0) {
                    x++;
                    if (x == 1) {
                        MarkerOptions marker = new MarkerOptions()
                                .position(
                                        new LatLng(arg0.latitude,
                                                arg0.longitude))
                                .title("Wetland Area")
                                .snippet(
                                        "Latitude: "
                                                + String.valueOf(arg0.latitude)
                                                + "\n Longitude: "
                                                + String.valueOf(arg0.longitude));
                        marker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        googleMap.addMarker(marker);
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "You can not select more the one landmarks at once",
                                Toast.LENGTH_SHORT).show();
                        allowPropmt();
                    }
                }
            });

        }
    }
    
    //Method for button of Cancelling the the area identification
     private void addListenerCancel() {
            // TODO Auto-generated method stub
            
            buttonCancel=(Button)findViewById(R.id.butCancel);
            buttonCancel.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent ci = new Intent(getApplicationContext(), WetlandTrackr.class);
                    startActivity(ci);
    
                }
            });
            
        }

    // If the user want to exit from the app
    public void allowPropmt() {
        new AlertDialog.Builder(this)
                .setMessage("Do you what to choose a different landmark?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                startActivity(new Intent(
                                        getApplicationContext(),
                                        LandAccess.class).putExtra("latitude",
                                        String.valueOf(latitude)).putExtra(
                                        "longitude", String.valueOf(longitude)));
                                x=0;
                            }
                        }).setNegativeButton("No", null).show();
    }

    // We need to let the users click on the map to give the location

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    public void Toaster(String bread) {
        Toast.makeText(LandAccess.this, bread, Toast.LENGTH_SHORT).show();

    }

    // For the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.land_access, menu);
        return true;
    }

}
