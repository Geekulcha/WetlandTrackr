package com.geekulcha.wetlandtrackr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	private EditText username;
	private EditText password;
	private Button login;
	private SharedPreferences pre;
	private Editor ed;
	private String url = "http://wlt.geekulcha.com/apis/login.php?json=";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setFields();
	}

	private void setFields() {
		pre = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		ed = pre.edit();
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		login = (Button) findViewById(R.id.login);
		login.setOnClickListener(new btnLogin());

		/*
		 * if (pre.contains("logged")) { startActivity(new Intent(Login.this,
		 * CaptureImage.class)); finish(); }
		 */

	}

	class btnLogin implements OnClickListener {

		@Override
		public void onClick(View v) {
			RequestQueue queue = Volley
					.newRequestQueue(getApplicationContext());
			JSONObject js = new JSONObject();

			try {
				js.put("username", username.getText().toString());

				js.put("password", password.getText().toString());
				Log.d("TAG", js.toString());
				StringRequest request = new StringRequest(Request.Method.POST,
						url + js, new Response.Listener<String>() {

							@Override
							public void onResponse(String arg0) {
								if (arg0 != null) {
									voly(arg0);
								}

							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError arg0) {
								// TODO Auto-generated method stub
								Log.d("TAG", arg0.toString());
								toaster("Please check your network connection");
							}
						});
				queue.add(request);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void toaster(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
				.show();
	}

	private void voly(String arg0) {
		try {
			JSONObject obj = new JSONObject(arg0);
			if (obj.getString("success").equals("1")) {

				JSONArray array = obj.getJSONArray("users");
				JSONObject jso = new JSONObject();
				for (int i = 0; i < array.length(); ++i) {
					jso = array.getJSONObject(i);
					Log.i("TAG", jso.getString("userID"));
					ed.putInt("userID", jso.getInt("userID"));
					ed.putString("logged", "logged");
					ed.commit();

					startActivity(new Intent(Login.this, CaptureImage.class));
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
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.logout) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
