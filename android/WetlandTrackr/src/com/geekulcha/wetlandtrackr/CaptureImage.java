package com.geekulcha.wetlandtrackr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CaptureImage extends Activity {
	final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
	private String TAG = CaptureImage.class.getSimpleName();
	Uri imageUri;
	static TextView imageDetails = null;
	public static ImageView image = null;
	CaptureImage CameraActivity = null;
	private static final int CAM_REQUREST = 1;
	public Button take;
	Bitmap thumbnail;
	File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture_image);

		setFields();
	}

	private void setFields() {
		CameraActivity = this;

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CAM_REQUREST && resultCode == RESULT_OK) {

			Log.d(TAG, data.getExtras().get("data").toString());

			thumbnail = (Bitmap) data.getExtras().get("data");
			image.setImageBitmap(thumbnail);
			OutputStream op = null;
			if (thumbnail != null) {
				Toast.makeText(getApplicationContext(), "ok "+thumbnail.getByteCount(), Toast.LENGTH_LONG).show();
				
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
