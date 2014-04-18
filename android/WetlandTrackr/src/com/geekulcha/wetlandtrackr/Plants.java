package com.geekulcha.wetlandtrackr;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

public class Plants extends Activity {
	private SharedPreferences pre;
	private ImageView image;
	private EditText name;
	private Button take;
	private EditText description;
	private Button submit;
	private int userID;
	private Bitmap thumbnail;
	private int wet_id;
	private String jsonImage;
	private String TAG = Plants.class.getSimpleName();
	private String url = "http://wlt.geekulcha.com/apis/add_plant.php?json=";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plants);
		setField();
	}

	private void setField() {
		pre = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		take = (Button) findViewById(R.id.take);
		userID = pre.getInt("userID", 0);
		submit = (Button) findViewById(R.id.submit);
		wet_id = getIntent().getExtras().getInt("wet_id");
		image = (ImageView) findViewById(R.id.image);
		name = (EditText) findViewById(R.id.name);
		description = (EditText) findViewById(R.id.description);
		submit.setOnClickListener(new btnSubmit());
		take.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImage();

			}
		});
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

	class btnSubmit implements OnClickListener {

		@Override
		public void onClick(View v) {
			JSONObject jo = new JSONObject();

			try {
				jo.put("pictures", (jsonImage != null) ? "no-image" : jsonImage);
				jo.put("name", name.getText().toString());
				jo.put("description", description.getText().toString());
				jo.put("userID", userID);
				jo.put("wet_id", wet_id);

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

	private void selectImage() {

		final CharSequence[] options = { "Take Photo", "Choose from Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(Plants.this);

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

	private void voly(String arg0) {
		try {
			JSONObject obj = new JSONObject(arg0);
			if (obj.getString("success").equals("1")) {
				Log.i("TAG", obj.toString());
				JSONArray array = obj.getJSONArray("wetland_data");
				JSONObject jso = new JSONObject();
				for (int i = 0; i < array.length(); ++i) {
					jso = array.getJSONObject(i);

					Toast.makeText(getApplicationContext(),
							jso.getString("message"), Toast.LENGTH_LONG).show();
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
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.plants, menu);
		return true;
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
		}
		return super.onOptionsItemSelected(item);
	}

}
