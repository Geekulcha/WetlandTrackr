package com.geekulcha.wetlandtrackr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CaptureImage extends Activity {
	private String TAG = CaptureImage.class.getSimpleName();
	Uri imageUri;
	static TextView imageDetails = null;
	public static ImageView image = null;
	CaptureImage CameraActivity = null;
	private static final int CAM_REQUREST = 1;
	public Button take;
	Bitmap thumbnail;
	private Button access;
	private EditText comment;
	private Button submit;
	private Button cancel;
	File file;
	private SharedPreferences pre;
	private Editor ed;
	private String url = "http://192.168.1.136/add_wetlandr/login.php?json=";
	LocationManager locMan;
	double lat, lng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture_image);
		if (isNetworkAvailable()) {

			// get location manager
			locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			// get last location
			Location lastLoc = locMan
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			lat = lastLoc.getLatitude();
			lng = lastLoc.getLongitude();

			// Toaster(String.valueOf(lat) + " \n " + String.valueOf(lng));
		} else {

		}

		setFields();
	}

	// Method to check network availbility
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	private void setFields() {
		pre = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		access = (Button) findViewById(R.id.access);
		access.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						LandAccess.class).putExtra("latitude",
						String.valueOf(lat)).putExtra("longitude",
						String.valueOf(lng)));

			}
		});
		comment = (EditText) findViewById(R.id.comment);

		submit = (Button) findViewById(R.id.submit);
		cancel = (Button) findViewById(R.id.cancel);
		imageDetails = (TextView) findViewById(R.id.imageDetails);
		image = (ImageView) findViewById(R.id.image);
		take = (Button) findViewById(R.id.take);

		take.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Calendar cal = Calendar.getInstance();
				file = new File(cal.getTimeInMillis() + ".jpg");
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					file.delete();
					try {
						file.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				imageUri = Uri.fromFile(file);

				Log.d(TAG, "Freebies " + file);

				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAM_REQUREST);

			}
		});
	}

	class btnSubmit implements OnClickListener {

		@Override
		public void onClick(View v) {
			JSONObject jo = new JSONObject();

			try {
				jo.put("lat", lat);

				jo.put("lon", lng);
				jo.put("userID", pre.getInt("userID", 0));

				jo.put("comments", comment.getText().toString());

				jo.put("pictures", "2");

				RequestQueue queue = Volley
						.newRequestQueue(getApplicationContext());
				StringRequest request = new StringRequest(Request.Method.POST,
						url + jo, new Response.Listener<String>() {

							@Override
							public void onResponse(String arg0) {

								try {
									JSONObject obj = new JSONObject(arg0);

									if (obj.getString("success").equals("1")) {
										voly(arg0);
									} else {
										Toast.makeText(getApplicationContext(),
												obj.getString("message"),
												Toast.LENGTH_SHORT).show();
										Log.i("TAG", obj.getString("message"));
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError arg0) {
								// TODO Auto-generated method stub
								Log.i("TAG ERROR", arg0.toString());
							}
						});

				queue.add(request);
				Log.i("TAG", jo.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void allow(int id) {
		new AlertDialog.Builder(this)
				.setMessage("Any plants growing on the wet land?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								startActivity(new Intent(
										getApplicationContext(),
										CaptureImage.class).putExtra("wet_id",
										id));
							}
						}).setNegativeButton("No", null).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CAM_REQUREST && resultCode == RESULT_OK) {

			Log.d(TAG, data.getExtras().get("data").toString());

			thumbnail = (Bitmap) data.getExtras().get("data");
			image.setImageBitmap(thumbnail);
			OutputStream op = null;
			if (thumbnail != null) {
				Toast.makeText(getApplicationContext(),
						"ok " + thumbnail.getByteCount(), Toast.LENGTH_LONG)
						.show();

			}
		}

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		thumbnail = (Bitmap) getLastNonConfigurationInstance();
		image.setImageBitmap(thumbnail);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// TODO Auto-generated method stub
		return thumbnail;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.capture_image, menu);
		return true;
	}

	private void voly(String arg0) {
		try {
			JSONObject obj = new JSONObject(arg0);
			if (obj.getString("success").equals("1")) {

				JSONArray array = obj.getJSONArray("wetland_data");
				JSONObject jso = new JSONObject();
				for (int i = 0; i < array.length(); ++i) {
					jso = array.getJSONObject(i);

					allow(jso.getInt("id"));
				}
			} else {
				Toast.makeText(getApplicationContext(),
						obj.getString("message"), Toast.LENGTH_SHORT).show();
				Log.i("TAG", obj.getString("message"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
