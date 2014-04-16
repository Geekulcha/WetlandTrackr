package com.geekulcha.wetlandtrackr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class CaptureImage extends Activity {
	private String TAG = CaptureImage.class.getSimpleName();
	Uri imageUri;
	static TextView imageDetails = null;
	public static ImageView image = null;

	public Button take;
	Bitmap thumbnail;
	private Button access;
	private EditText comment;
	private Button submit;
	private Button cancel;
	File file;
	private SharedPreferences pre;
	private String url = "http://192.168.1.140/wetlandtrackr/add_wetlandr.php?json=";
	LocationManager locMan;
	double lat, lng;
	private String jsonImage;

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
		submit.setOnClickListener(new btnSubmit());
		take.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImage();
			}
		});
	}

	class btnSubmit implements OnClickListener {

		@Override
		public void onClick(View v) {
			JSONObject jo = new JSONObject();

			try {
				jo.put("pictures", (jsonImage != null) ? "no-image" : jsonImage);

				jo.put("lat", lat);

				jo.put("lon", lng);
				jo.put("userID", pre.getInt("userID", 0));

				jo.put("comments", comment.getText().toString());

				RequestQueue queue = Volley
						.newRequestQueue(getApplicationContext());
				Log.w(TAG, url + jo);
				StringRequest request = new StringRequest(url + jo,
						new Response.Listener<String>() {

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

	private String getStringFromBitmap(Bitmap bitmapPicture) {
		/*
		 * This functions converts Bitmap picture to a string which can be
		 * JSONified.
		 */
		final int COMPRESSION_QUALITY = 100;
		String encodedImage;
		ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
		bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
				byteArrayBitmapStream);
		byte[] b = byteArrayBitmapStream.toByteArray();
		encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
		return encodedImage;
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
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.capture_image, menu);
		return true;
	}

	private void voly(String arg0) {
		try {
			JSONObject obj = new JSONObject(arg0);
			if (obj.getString("success").equals("1")) {
				Log.i("TAG", obj.toString());
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {

				File f = new File(Environment.getExternalStorageDirectory()
						.toString());

				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}

				try {
					if (thumbnail != null && !thumbnail.isRecycled()) {
						thumbnail = null;
					}

					thumbnail = BitmapFactory.decodeFile(f.getAbsolutePath());

					Bitmap newImage = Bitmap.createScaledBitmap(thumbnail, 800,
							800, false);

					image.setBackgroundResource(0);
					image.setImageBitmap(newImage);

					f.delete();

					jsonImage = getStringFromBitmap(newImage);
					String path = android.os.Environment
							.getExternalStorageDirectory()
							+ File.separator
							+ "Phoenix" + File.separator + "default";

					OutputStream outFile = null;

					File file = new File(path, String.valueOf(System
							.currentTimeMillis()) + ".jpg");

					try {
						outFile = new FileOutputStream(file);

						thumbnail.compress(Bitmap.CompressFormat.JPEG, 85,
								outFile);

						outFile.flush();

						outFile.close();

					} catch (FileNotFoundException e) {

						e.printStackTrace();

					} catch (IOException e) {

						e.printStackTrace();

					} catch (Exception e) {

						e.printStackTrace();

					}

				} catch (Exception e) {

					e.printStackTrace();

				}

			} else if (requestCode == 2) {

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex); // file path of
																	// selected
																	// image
				cursor.close();
				// Convert file path into bitmap image using below line.

				thumbnail = BitmapFactory.decodeFile(filePath);

				Bitmap newBitmap = Bitmap.createScaledBitmap(thumbnail, 800,
						800, false);

				// put bitmapimage in your imageview
				image.setImageBitmap(newBitmap);
				jsonImage = getStringFromBitmap(newBitmap);

			}

		}
	}

	private void selectImage() {

		final CharSequence[] options = { "Take Photo", "Choose from Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(CaptureImage.this);

		builder.setTitle("Add Photo!");

		builder.setItems(options, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int item) {

				if (options[item].equals("Take Photo")) {

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment
							.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);

				} else if (options[item].equals("Choose from Gallery")) {

					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					startActivityForResult(intent, 2);

				} else if (options[item].equals("Cancel")) {

					dialog.dismiss();

				}

			}

		});

		builder.show();

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
