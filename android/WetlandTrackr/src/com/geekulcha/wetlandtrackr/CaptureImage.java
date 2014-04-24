package com.geekulcha.wetlandtrackr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
import android.location.Criteria;
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
import com.geekulcha.wetlandtrackr.locators.GPSTracker;

public class CaptureImage extends Activity {
	private String TAG = CaptureImage.class.getSimpleName();
	Uri imageUri;
	public static ImageView image = null;
	public Button take;
	Bitmap thumbnail;
	private Button access;
	private EditText comment;
	private Button submit;
	private Button cancel;
	File file;
	private SharedPreferences pre;
	private String url = "http://wlt.geekulcha.com/apis/add_wetlandr.php?json=";
	LocationManager locMan;
	double lat, lng;
	private String jsonImage;
	private GPSTracker tracker;
	private String landImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture_image);
		tracker = new GPSTracker(CaptureImage.this);

		lat = tracker.getLatitude();
		lng = tracker.getLongitude();
		Log.w(TAG, lng + " " + lat);

		// Toaster(String.valueOf(lat) + " \n " + String.valueOf(lng));
		Log.e(TAG, lat + " " + lng);
		setFields();
	}

	private void toaster(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
				.show();
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
		cancel.setOnClickListener(new CancelBtn());
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

	class CancelBtn implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(CaptureImage.this, CaptureImage.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			startActivity(intent);

		}

	}

	class btnSubmit implements OnClickListener {

		@Override
		public void onClick(View v) {
			JSONObject jo = new JSONObject();
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
			landImage = f.format(cal.getTime());

			try {
				if (jsonImage != null) {
					jo.put("picture", landImage + ".jpg");
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
									Log.w(TAG, arg0);
									voly(arg0);
								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError arg0) {
									// TODO Auto-generated method stub
									toaster("Please check your network connection");
									Log.i("TAG ERROR", arg0.toString());
								}
							});

					queue.add(request);

					Log.i("TAG", jo.toString());
				} else {
					toaster("Required field(s)");
				}
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

	private void allow(final int Id) {
		Log.w(TAG, Id + "");
		new AlertDialog.Builder(this)
				.setMessage("Any plants growing on the wet land?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								startActivity(new Intent(
										getApplicationContext(), Plants.class)
										.putExtra("wet_id", Id));
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
				savePhotoToServer(jsonImage);
				JSONArray array = obj.getJSONArray("wetland_data");
				JSONObject jso = new JSONObject();
				for (int i = 0; i < array.length(); ++i) {
					jso = array.getJSONObject(i);
					Log.w(TAG, jso.getInt("id") + "");
					allow(jso.getInt("id"));
				}
			} else {
				toaster(obj.getString("message"));
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

	private void savePhotoToServer(String value) {
		Log.w(TAG + 12, value);
		final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("image", value));
		nameValuePairs.add(new BasicNameValuePair("folder", "0"));
		nameValuePairs.add(new BasicNameValuePair("picName", landImage));

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(
							"http://wlt.geekulcha.com/apis/upload_image.php");
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					// final String the_string_response =
					// convertResponseToString(response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							toaster("Picture successfully uploaded");
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							toaster("Picture not successfully uploaded");
						}
					});
					System.out.println("Error in http connection "
							+ e.toString());
				}

			}
		});
		thread.start();

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
		if (id == R.id.logout) {
			pre.edit().clear();
			startActivity(new Intent(getApplicationContext(), Login.class));
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
